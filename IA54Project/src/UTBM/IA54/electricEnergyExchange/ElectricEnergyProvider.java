package UTBM.IA54.electricEnergyExchange;


import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;

import UTBM.IA54.capacity.ComputeElectricEnergyCapacity;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.message.EnergyRequestMessage;
import UTBM.IA54.message.ProposalEnergyMessage;
import UTBM.IA54.message.ProposalFinalizedEnergyMessage;

public class ElectricEnergyProvider extends Role {
	
	private State state = null;
	
	public ElectricEnergyProvider() {
		this.addObtainCondition(new HasAllRequiredCapacitiesCondition(ComputeElectricEnergyCapacity.class));
	}
	
	@Override
	public Status activate(Object... params) {
		this.state = State.WAITING_REQUEST;
		
		return StatusFactory.ok(this);
	}


	@Override
	public Status live() {
		this.state = this.run();
		
		return StatusFactory.ok(this);
	}
	
	private State run() {
		switch(this.state) {
		case WAITING_REQUEST:
			Request request = null;
			
			// look at the mailbox
			for(Message m : this.getMessages(EnergyRequestMessage.class)) {
				request = ((EnergyRequestMessage) m).getRequest();
				break;
			}
			
			// if there is no energy request => waiting state again
			if(request.equals(null)) {
				return State.WAITING_REQUEST;
			} else {
				try {
					CapacityContext cc = this.executeCapacityCall(ComputeElectricEnergyCapacity.class, request);
					
					if(cc.isResultAvailable()) {
						Proposal p = (Proposal)cc.getOutputValueAt(0);
						
						// Send proposal to consumer				
						this.sendMessage(request.getConsumer(), new ProposalEnergyMessage(p));
						
						return State.WAITING_ANSWER_PROPOSAL;
					}
				} catch (Throwable e) {
					error(e.getLocalizedMessage());
					return State.WAITING_REQUEST;
				}
			}					
			
		case WAITING_ANSWER_PROPOSAL:
			Proposal proposal = null;
			
			//look at the mail box - if no mail => waiting again else => look at the mail content
			for(Message m : this.getMessages(ProposalFinalizedEnergyMessage.class)) {
				proposal = ((ProposalFinalizedEnergyMessage) m).getProposalFinalized().getProposal();
				
				if(!proposal.equals(null)) {
					// send signal to agent to inform it of the transaction
				}
			}
			return State.WAITING_REQUEST;
		default:
			return this.state;
		}
	}

	private enum State {
		/**
		 * Wait some request. Continue to produce energy
		 * It sends a proposal when it receive a request 
		 */
		WAITING_REQUEST,
		/**
		 * Wait an answer from consumer
		 */
		WAITING_ANSWER_PROPOSAL
	}
} 