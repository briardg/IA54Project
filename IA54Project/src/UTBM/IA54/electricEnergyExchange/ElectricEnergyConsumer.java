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

import UTBM.IA54.capacity.ComputeRequestCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.ProposalFinalized;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.message.EnergyRequestMessage;
import UTBM.IA54.message.ProposalEnergyMessage;
import UTBM.IA54.message.ProposalFinalizedEnergyMessage;

/**
 * Role which defines the behavior of a Consumer of electric energy
 * @author Anthony
 *
 */
public class ElectricEnergyConsumer extends Role {
	/**
	 * the current state of the role
	 */
	private State state = null;
	private int timeUnit;
	/**
	 * current Request
	 */
	private Request currentRequest;
	private int counter = 0;
	private int unitTimeToWait;
	private final int MAX_UNIT_TIME_TO_WAIT = 3;
	private final int MAX_UNIT_TIME_TO_WAIT_PROPOSAL = 10;

	/**
	 * 
	 */
	public ElectricEnergyConsumer() {
		this.addObtainCondition(
				new HasAllRequiredCapacitiesCondition(
						Arrays.asList(
								ComputeRequestCapacity.class, 
								FindBestProposalCapacity.class
						)
				)
		);
		
		this.timeUnit = 0;
		this.currentRequest = null;
		this.unitTimeToWait = 0;
	}

	@Override
	public Status activate(Object... params) {
		this.state = State.WAITING;

		System.out.println(this.getPlayer().getName()+" consumer get initialized");

		return super.activate(params);
	}

	@Override
	public Status live() {
		if(this.counter < 40)
			this.state = this.run();
		this.counter++;
		return StatusFactory.ok(this);
	}
	
	/**
	 * 
	 * @return the last state of the agent
	 */
	private State run() {
		switch(this.state) {
		case WAITING:
			this.timeUnit = 0;
			try {
				CapacityContext cc = this.executeCapacityCall(ComputeRequestCapacity.class, (Object)null);
								
				if(cc.isResultAvailable()) {
					this.currentRequest = (Request)cc.getOutputValueAt(0);
					
					System.out.println(this.getPlayer().getName()+" consumer has created request :"+this.currentRequest);
					
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
			this.broadcastMessage(ElectricEnergyProvider.class, new EnergyRequestMessage(this.currentRequest));
			
			System.out.println(this.getPlayer().getName()+" consumer : requets sent => Waiting proposal");
			
			return State.WAITING_PROPOSAL;
			
		case WAITING_PROPOSAL:
			if(this.unitTimeToWait >= this.MAX_UNIT_TIME_TO_WAIT) {
				this.unitTimeToWait = 0;
				
				List<Proposal> proposals = new ArrayList<Proposal>();
				List<Proposal> proposalsUpToDate = new ArrayList<Proposal>();
				// This list will content all expired proposal (these proposals answer to an old request)
				List<Message> proposalsExpired = new ArrayList<Message>();
				
				// get messages from providers			
				for(Message m : this.getMessages()) {
					if(m instanceof ProposalEnergyMessage) {
						Proposal p = ((ProposalEnergyMessage)m).getProposal();
						proposals.add(p);
						if(this.currentRequest.equals(p.getRequest())) {
							// Put only proposals which response to the current request
							proposalsUpToDate.add(p);
						} else {
							// Else put them to this list. They will be removed because they are expired
							proposalsExpired.add(m);
						}
					}
				}		
											
				if(proposalsUpToDate.size() > 0) {
					try {
						CapacityContext cc = this.executeCapacityCall(FindBestProposalCapacity.class, proposalsUpToDate.toArray());
						
						if(cc.isResultAvailable()) {
							// Get best proposal
							ArrayList<Proposal> props = (ArrayList<Proposal>)cc.getOutputValueAt(0);
							
							System.out.println(this.getPlayer().getName()+" consumer: number of proposals : "+proposalsUpToDate.size()+", best proposal from: ");
							
							for(Proposal a : props)
								System.out.println(a.getProvider().getPlayer().getName());
							
							// send answer to all providers
							for(Proposal prop : proposals) {
								if(props.contains(prop)) {
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

					return State.WAITING;
				}
				
				proposalsUpToDate = null;
				proposals = null;
				
				// Remove messages expired
				for(Message m : proposalsExpired) {
					this.getMailbox().remove(m);				
				}
	
				this.timeUnit++;
				
				if(this.timeUnit >= this.MAX_UNIT_TIME_TO_WAIT_PROPOSAL) {
					// no answer to the request message => send new request
					return State.WAITING;
				}
			} else {
				this.unitTimeToWait++;
				this.timeUnit++;
			}
			// If no proposals messages in mailbox
			return State.WAITING_PROPOSAL;
			
		default:
			return this.state;
		}
	}

	/**
	 * Enum of the different states of the role
	 * @author Anthony
	 *
	 */
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
