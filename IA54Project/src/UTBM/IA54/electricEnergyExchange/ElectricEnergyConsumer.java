package UTBM.IA54.electricEnergyExchange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeElectricEnergyNeededCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.ProposalFinalized;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.influence.ConsumeEnergyInfluence;
import UTBM.IA54.message.EnergyRequestMessage;
import UTBM.IA54.message.ProposalEnergyMessage;
import UTBM.IA54.message.ProposalFinalizedEnergyMessage;

public class ElectricEnergyConsumer extends Role {
	
	private State state = null;
	private int timeUnit;
	private Request currentRequest;
	private int counter = 0;

	public ElectricEnergyConsumer() {
		this.addObtainCondition(new HasAllRequiredCapacitiesCondition(Arrays.asList(ComputeElectricEnergyNeededCapacity.class, FindBestProposalCapacity.class)));
		this.timeUnit = 0;
		this.currentRequest = null;
	}

	@Override
	public Status activate(Object... params) {
		this.state = State.WAITING;

		return super.activate(params);
	}

	@Override
	public Status live() {
		if(this.counter < 10)
			this.state = this.run();
		this.counter++;
		return StatusFactory.ok(this);
	}
	
	private State run() {
		switch(this.state) {
		case WAITING:
			System.out.println("consumer state : waiting");
			this.timeUnit = 0;
			try {
				CapacityContext cc = this.executeCapacityCall(ComputeElectricEnergyNeededCapacity.class, (Object)null);
								
				if(cc.isResultAvailable()) {
					this.currentRequest = (Request)cc.getOutputValueAt(0);
					
					if(this.currentRequest != null) {
						this.currentRequest.setConsumer(this.getAddress());
						return State.SEND_ENERGY_REQUEST;
					} else {
						return State.WAITING;
					}
				}
			} catch (Throwable e) {
				error(e.getLocalizedMessage());
				return State.WAITING;
			}
			
			return State.SEND_ENERGY_REQUEST;
			
		case SEND_ENERGY_REQUEST:
			// Send request to all electric energy providers
			System.out.println("request sent");
			this.broadcastMessage(ElectricEnergyProvider.class, new EnergyRequestMessage(this.currentRequest));
			
			return State.WAITING_PROPOSAL;
			
		case WAITING_PROPOSAL:
			System.out.println("consumer state : waiting proposal");
			List<Proposal> proposals = new ArrayList<Proposal>();
			List<Proposal> proposalsUpToDate = new ArrayList<Proposal>();
			
			// get messages from providers			
			for(Message m : this.getMessages()) {
				if(m instanceof ProposalEnergyMessage) {
					Proposal p = ((ProposalEnergyMessage)m).getProposal();
					proposals.add(p);
					if(this.currentRequest.equals(p.getRequest())) {
						// Put only proposals which response to the current request
						proposalsUpToDate.add(p);
					}
				}
			}			
						
			if(proposals.size() > 0) {
				System.out.println("consumer is finding best proposal");
				try {
					CapacityContext cc = this.executeCapacityCall(FindBestProposalCapacity.class, proposalsUpToDate.toArray());
					
					if(cc.isResultAvailable()) {
						Proposal p = (Proposal)cc.getOutputValueAt(0);
						
						// send answer to all providers
						for(Proposal prop : proposals) {
							if(p.getProvider().equals(prop.getProvider())) {
								// positive answer
								this.sendMessage(prop.getProvider(), new ProposalFinalizedEnergyMessage(new ProposalFinalized(prop)));
							} else {
								// negative answer
								this.sendMessage(prop.getProvider(), new ProposalFinalizedEnergyMessage(new ProposalFinalized(null)));
							}
						}
					}
				} catch (Throwable e) {
					error(e.getLocalizedMessage());
					return State.WAITING;
				}	
				// notice the agent that we have consumed some energy
				this.fireSignal(new ConsumeEnergyInfluence(this, this.currentRequest));
				System.out.println("consumer fire signal");
				return State.WAITING;
			}

			this.timeUnit++;
			
			if(this.timeUnit > 10) {
				// no answer to the request message => send new request
				return State.WAITING;
			}
			// If no proposals messages in mailbox
			return State.WAITING_PROPOSAL;
			
		default:
			return this.state;
		}
	}

	private enum State {
		/**
		 *  wait if the role doesn't need energy
		 */
		WAITING,
		/**
		 * the role need energy => Send request to all energy providers role
		 */
		SEND_ENERGY_REQUEST,
		/**
		 * Choose the best proposal from all the energy providers' answer
		 */
		WAITING_PROPOSAL;
	}
}
