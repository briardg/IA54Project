package UTBM.IA54.electricEnergyExchange;


import java.util.ArrayList;
import java.util.Arrays;

import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.mailbox.Mailbox;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;

import UTBM.IA54.capacity.ComputeElectricEnergyProvidedCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacityImpl;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.influence.ConsumeEnergyInfluence;
import UTBM.IA54.message.EnergyRequestMessage;
import UTBM.IA54.message.ProposalEnergyMessage;
import UTBM.IA54.message.ProposalFinalizedEnergyMessage;

public class ElectricEnergyProvider extends Role {
	
	private State state = null;
	
	public ElectricEnergyProvider() {
		this.addObtainCondition(new HasAllRequiredCapacitiesCondition(Arrays.asList(ComputeElectricEnergyProvidedCapacity.class, FindBestRequestCapacity.class)));
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
			ArrayList<Request> requests = new ArrayList<Request>();
			ArrayList<EnergyRequestMessage> messagesList = new ArrayList<EnergyRequestMessage>();
			
			// look at the mailbox
			for(Message m : this.peekMessages(EnergyRequestMessage.class)) {
				requests.add(((EnergyRequestMessage) m).getRequest());
				messagesList.add((EnergyRequestMessage)m);
				break;
			}
			
			// if there is no energy request => WAITING_REQUEST state again
			if(requests.size() == 0) {
				return State.WAITING_REQUEST;
			} else {
				try {
					// find the best request
					CapacityContext cc1 = this.executeCapacityCall(FindBestRequestCapacityImpl.class, requests);
					
					if(cc1.isResultAvailable()) {
						Request request = (Request)cc1.getOutputValueAt(0);
					
						if(request != null) {
							// create a proposal
							CapacityContext cc2 = this.executeCapacityCall(ComputeElectricEnergyProvidedCapacity.class, request);
							
							if(cc2.isResultAvailable()) {
								Proposal p = (Proposal)cc2.getOutputValueAt(0);
								p.setProvider(this.getAddress());
								
								// Send proposal to consumer				
								this.sendMessage(request.getConsumer(), new ProposalEnergyMessage(p));
								
								// Remove request from the mailbox
								this.getMailbox().remove(this.findRequestMessageFromListAccordingToARequest(messagesList, request));
								
								return State.WAITING_ANSWER_PROPOSAL;
							}
						}
					}					
				} catch (Throwable e) {
					error(e.getLocalizedMessage());
					return State.WAITING_REQUEST;
				}
			}					
			
		case WAITING_ANSWER_PROPOSAL:
			Proposal proposal = null;
			
			boolean proposalFinalizedConsumed = false;
			
			//look at the mail box - if no mail => waiting again else => look at the mail content
			for(Message m : this.getMessages(ProposalFinalizedEnergyMessage.class)) {
				proposal = ((ProposalFinalizedEnergyMessage) m).getProposalFinalized().getProposal();
				// if proposal == null, the consume rejected the proposal
				
				if(proposal != null) {
					// Consumer has accepted the proposal
					
					// notice the agent that we have provided some energy to a consumer
					this.fireSignal(new ConsumeEnergyInfluence(this, proposal.getRequest()));
				}
				proposalFinalizedConsumed = true;
			}
			
			if(proposalFinalizedConsumed) {
				// If we had an answer to our proposal
				return State.WAITING_REQUEST;				
			} else {
				// no answer
				return State.WAITING_ANSWER_PROPOSAL;
			}
		default:
			return this.state;
		}
	}
	
	/**
	 * Find EnergyRequestMessage from an ArrayList of EnergyRequestMessage 
	 * according to a Request (an EnergyRequestMessage contents a Request)
	 * @param messagesList ArrayList<EnergyRequestMessage>
	 * @param request Request
	 * @return EnergyRequestMessage found
	 */
	private EnergyRequestMessage findRequestMessageFromListAccordingToARequest(ArrayList<EnergyRequestMessage> messagesList, Request request) {
		EnergyRequestMessage requestMessage = null;
		
		for(EnergyRequestMessage m : messagesList) {
			if(request.equals(((EnergyRequestMessage)m.getContent()).getRequest())) {
				requestMessage = m;
				break;
			}
		}
		
		return requestMessage;
		
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