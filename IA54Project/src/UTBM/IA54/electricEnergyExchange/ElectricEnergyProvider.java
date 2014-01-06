package UTBM.IA54.electricEnergyExchange;


import java.util.ArrayList;
import java.util.Arrays;

import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;

import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacity;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.capacity.UpdateProviderAttrCapacity;
import UTBM.IA54.message.RequestEnergyMessage;
import UTBM.IA54.message.ProposalEnergyMessage;
import UTBM.IA54.message.ProposalFinalizedEnergyMessage;

/**
 * Role which defines the behavior of a Provider of electric energy
 * @author Anthony et Gautier
 *
 */
public class ElectricEnergyProvider extends Role {
	/**
	 * the state of the role
	 */
	private State state = null;
	private int counter = 0;
	private int unitTimeToWait;
	private final int MAX_UNIT_TIME_TO_WAIT = 3;
	
	/**
	 * 
	 */
	public ElectricEnergyProvider() {
		this.addObtainCondition(
				new HasAllRequiredCapacitiesCondition(
						Arrays.asList(
								ComputeProposalCapacity.class, 
								FindBestRequestCapacity.class, 
								UpdateProviderAttrCapacity.class
						)
				)
		);
		
		this.unitTimeToWait = 0;
	}
	
	@Override
	public Status activate(Object... params) {
		this.state = State.WAITING_REQUEST;
		
		System.out.println(this.getPlayer().getName()+" provider is initialized => waiting request");

		return super.activate(params);
	}

	@Override
	public Status live() {
		//if(this.counter < 60)
			this.state = this.run();
		//this.counter++;
		return StatusFactory.ok(this);
	}

	/**
	 * 
	 * @return the state of the role
	 */
	private State run() {
		switch(this.state) {
		case WAITING_REQUEST:
			// Wait MAX_UNIT_TIME_TO_WAIT unit time to receive more requests
			if(this.unitTimeToWait >= this.MAX_UNIT_TIME_TO_WAIT) {
				this.unitTimeToWait = 0;
				
				ArrayList<Request> requests = new ArrayList<Request>();
				//ArrayList<EnergyRequestMessage> messagesList = new ArrayList<EnergyRequestMessage>();
				//ArrayList<Message> messageToRemove = new ArrayList<Message>();
				
				if(this.hasMessage()) {		
					//Mailbox mailBox = this.getMailbox();
					
					System.out.print(this.getPlayer().getName()+" provider : request list received from :");
					// look at the mailbox
					for(Message m : this.getMessages(RequestEnergyMessage.class)) {
						RequestEnergyMessage message = (RequestEnergyMessage)m;
	
						//Don't deal with this request if the sender is the receiver
						if(message.getRequest().getConsumer().getPlayer().getName() != this.getPlayer().getName()) {
							requests.add(message.getRequest());
							System.out.print(", "+message.getRequest().getConsumer().getPlayer().getName());							
						}
					}
					System.out.println("");
									
					// if there is no request => WAITING_REQUEST state again
					if(requests.size() == 0) {
						return State.WAITING_REQUEST;
					} else {
						try {
							// find the best request
							CapacityContext cc1 = this.executeCapacityCall(FindBestRequestCapacity.class, requests);
							
							if(cc1.isResultAvailable()) { 
								Request request = (Request)cc1.getOutputValueAt(0);
							
								if(request != null) {
									// create a proposal
									CapacityContext cc2 = this.executeCapacityCall(ComputeProposalCapacity.class, request);
									
									if(cc2.isResultAvailable()) {
										Proposal p = (Proposal)cc2.getOutputValueAt(0);
										p.setProvider(this.getAddress());
		
										// Send proposal to consumer	
										System.out.println(this.getPlayer().getName()+" provider : Send proposal to :"+p.getRequest().getConsumer().getPlayer().getName());
										this.sendMessage(request.getConsumer(), new ProposalEnergyMessage(p));
		
										return State.WAITING_ANSWER_PROPOSAL;
									}
								}
							}					
						} catch (Throwable e) {
							error(e.getLocalizedMessage());
							return State.WAITING_REQUEST;
						}
					}	
				}
			} else {
				this.unitTimeToWait++;
			}
			return State.WAITING_REQUEST;
			
		case WAITING_ANSWER_PROPOSAL:
			Proposal proposal = null;
			
			boolean proposalFinalizedConsumed = false;
			
			//look at the mail box - if no mail => waiting again else => look at the mail content
			for(Message m : this.getMessages(ProposalFinalizedEnergyMessage.class)) {
				if(m instanceof ProposalFinalizedEnergyMessage) {
					proposal = ((ProposalFinalizedEnergyMessage) m).getProposalFinalized().getProposal();
					
					// if proposal == null, the consume has rejected the proposal
					if(proposal != null) {
						System.out.println(this.getPlayer().getName()+" provider : consumer has accepted proposal");
						// Consumer has accepted the proposal												
						try {
							// notice the agent that we have provided some energy to a consumer
							this.executeCapacityCall(UpdateProviderAttrCapacity.class, proposal);												
						} catch (Throwable e) {
							error(e.getLocalizedMessage());
						}
					} else {
						System.out.println(this.getPlayer().getName()+" provider : consumer has rejected proposal");
					}
					proposalFinalizedConsumed = true;
				}
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
	 * Find an EnergyRequestMessage from an ArrayList of EnergyRequestMessage 
	 * according to a Request (an EnergyRequestMessage contents a Request)
	 * @param messagesList ArrayList<EnergyRequestMessage>
	 * @param request Request
	 * @return EnergyRequestMessage found
	 */
	private RequestEnergyMessage findRequestMessageFromListAccordingToARequest(ArrayList<RequestEnergyMessage> messagesList, Request request) {
		RequestEnergyMessage requestMessage = null;

		for(RequestEnergyMessage m : messagesList) {
			if(request.equals(((RequestEnergyMessage)m).getRequest())) {
				requestMessage = m;
				break;
			}
		}
		return requestMessage;		
	}

	/**
	 * Enum contenting the different states of the role
	 * @author Anthony et Gautier
	 *
	 */
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