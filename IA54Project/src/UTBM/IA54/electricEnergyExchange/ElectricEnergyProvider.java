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

import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacity;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.capacity.UpdateProviderAttrCapacity;
import UTBM.IA54.message.EnergyRequestMessage;
import UTBM.IA54.message.ProposalEnergyMessage;
import UTBM.IA54.message.ProposalFinalizedEnergyMessage;

public class ElectricEnergyProvider extends Role {
	
	private State state = null;
	private int counter = 0;
	private int unitTimeToWait;
	
	private final int MAX_UNIT_TIME_TO_WAIT = 3;
	
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

		return super.activate(params);
	}

	@Override
	public Status live() {
		if(this.counter < 20)
			this.state = this.run();
		this.counter++;
		return StatusFactory.ok(this);
	}

	private State run() {
		switch(this.state) {
		case WAITING_REQUEST:
			System.out.println(this.getPlayer().getName()+" provider : waiting request state");
			
			// Wait MAX_UNIT_TIME_TO_WAIT unit time to receive more requests
			if(this.unitTimeToWait >= this.MAX_UNIT_TIME_TO_WAIT) {
				this.unitTimeToWait = 0;
				
				ArrayList<Request> requests = new ArrayList<Request>();
				ArrayList<EnergyRequestMessage> messagesList = new ArrayList<EnergyRequestMessage>();
				ArrayList<Message> messageToRemove = new ArrayList<Message>();
				
				if(this.hasMessage()) {		
					Mailbox mailBox = this.getMailbox();
					
					System.out.print(this.getPlayer().getName()+" provider : request list received from :");
					// look at the mailbox
					for(Message m : mailBox.iterable(false)) {
						if(m instanceof EnergyRequestMessage) {
							EnergyRequestMessage message = (EnergyRequestMessage)m;
		
							// Remove message send by this role itself (we can t treat a request sent by this role itself)
							if(message.getRequest().getConsumer().getPlayer().getName() == this.getPlayer().getName()) {
								messageToRemove.add(m);
							} else {
								requests.add(message.getRequest());
								System.out.print(", "+message.getRequest().getConsumer().getPlayer().getName());
								messagesList.add(message);
							}
						}
					}
					
					// Remove messages send by this role itself
					for(Message m : messageToRemove) {
						this.getMailbox().remove(m);				
					}
										
					// if there is no energy request => WAITING_REQUEST state again
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
										System.out.println(this.getPlayer().getName()+" provider : Send proposal to consumer"+p);
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
				}
			} else {
				this.unitTimeToWait++;
			}
			return State.WAITING_REQUEST;
			
		case WAITING_ANSWER_PROPOSAL:
			System.out.println(this.getPlayer().getName()+" provider : waiting answer proposal state");
			Proposal proposal = null;
			
			boolean proposalFinalizedConsumed = false;
			
			//look at the mail box - if no mail => waiting again else => look at the mail content
			for(Message m : this.getMessages(ProposalFinalizedEnergyMessage.class)) {
				if(m instanceof ProposalFinalizedEnergyMessage) {
					proposal = ((ProposalFinalizedEnergyMessage) m).getProposalFinalized().getProposal();
					// if proposal == null, the consume has rejected the proposal
					
					if(proposal != null) {
						System.out.println(this.getPlayer().getName()+" provider : consumer accepted proposal");
						// Consumer has accepted the proposal
												
						try {
							// notice the agent that we have provided some energy to a consumer
							this.executeCapacityCall(UpdateProviderAttrCapacity.class, proposal.getRequest());												
						} catch (Throwable e) {
							error(e.getLocalizedMessage());
						}
					} else {
						System.out.println(this.getPlayer().getName()+" provider : consumer rejected proposal");
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
	 * Find EnergyRequestMessage from an ArrayList of EnergyRequestMessage 
	 * according to a Request (an EnergyRequestMessage contents a Request)
	 * @param messagesList ArrayList<EnergyRequestMessage>
	 * @param request Request
	 * @return EnergyRequestMessage found
	 */
	private EnergyRequestMessage findRequestMessageFromListAccordingToARequest(ArrayList<EnergyRequestMessage> messagesList, Request request) {
		EnergyRequestMessage requestMessage = null;

		for(EnergyRequestMessage m : messagesList) {
			if(request.equals(((EnergyRequestMessage)m).getRequest())) {
				requestMessage = m;
				break;
			}
		}
		System.out.println("\n message à supprimer : "+requestMessage+"\n");
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