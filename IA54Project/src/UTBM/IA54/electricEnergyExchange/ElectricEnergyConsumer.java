package UTBM.IA54.electricEnergyExchange;

import java.util.ArrayList;
import java.util.List;

import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.message.Message;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeEnergyNeededCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.ProposalFinalized;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.message.EnergyRequestMessage;
import UTBM.IA54.message.ProposalEnergyMessage;
import UTBM.IA54.message.ProposalFinalizedEnergyMessage;

public class ElectricEnergyConsumer extends Role {
	
	private State state = null;
	private double energyNeeded; 

	public ElectricEnergyConsumer() {
		this.addObtainCondition(new HasAllRequiredCapacitiesCondition(ComputeEnergyNeededCapacity.class));
		this.addObtainCondition(new HasAllRequiredCapacitiesCondition(FindBestProposalCapacity.class));
	}

	@Override
	public Status activate(Object... params) {
		this.state = State.WAITING;
		
		return StatusFactory.ok(this);
	}


	@Override
	public Status live() {
		this.state = this.run();
		
		return StatusFactory.ok(this);
	}
	
	private State run() {
		switch(this.state) {
		case WAITING:
			try {
				CapacityContext cc = this.executeCapacityCall(ComputeEnergyNeededCapacity.class, (Object)null);
								
				if(cc.isResultAvailable()) {
					this.energyNeeded = (double)cc.getOutputValueAt(0);
					
					if(this.energyNeeded > 0.0) {
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
			this.broadcastMessage(ElectricEnergyProvider.class, new EnergyRequestMessage(new Request(this.getAddress(), this.energyNeeded, 3)));
			
			return State.CHOOSING_PROPOSAL;
			
		case CHOOSING_PROPOSAL:
			List<Proposal> proposals = new ArrayList<Proposal>();
			
			// get messages from providers			
			for(Message m : this.getMessages(ProposalEnergyMessage.class)) {
				proposals.add(((ProposalEnergyMessage)m).getProposal());
			}
			// if proposal == null ?
			try {
				CapacityContext cc = this.executeCapacityCall(FindBestProposalCapacity.class, proposals.toArray());
				
				if(cc.isResultAvailable()) {
					Proposal p = (Proposal)cc.getOutputValueAt(0);
					
					// send answer to all providers
					for(Proposal prop : proposals) {
						if(p.getProvider().equals(prop.getProvider())) {
							this.sendMessage(prop.getProvider(), new ProposalFinalizedEnergyMessage(new ProposalFinalized(prop)));
						} else {
							this.sendMessage(prop.getProvider(), new ProposalFinalizedEnergyMessage(new ProposalFinalized(null)));
						}
					}
				}
			} catch (Throwable e) {
				error(e.getLocalizedMessage());
				return State.WAITING;
			}			
			
			return State.WAITING;
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
		CHOOSING_PROPOSAL;
	}
}
