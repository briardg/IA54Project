package UTBM.IA54.agents;

import java.util.ArrayList;
import java.util.Iterator;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agentsignal.QueuedSignalAdapter;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.ComputeElectricEnergyNeededCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacityImpl;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyConsumer;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;
import UTBM.IA54.influence.ProvideEnergyInfluence;
import UTBM.IA54.influence.ConsumeEnergyInfluence;

/**
 * Define a battery Agent
 * @author Anthony
 *
 */
public class BatteryAgent extends Agent{
	/**
	 * UUID
	 */
	private static final long serialVersionUID = 6940432570132410571L;
	/** 
	 * Energy stored by the battery
	 */
	private long energyStored;
	/**
	 * Signal listener. Triggered when battery consume electric energy (when it gets energy from provider)
	 */
	private final QueuedSignalAdapter<ProvideEnergyInfluence> signalConsumerListener = new QueuedSignalAdapter<ProvideEnergyInfluence>(ProvideEnergyInfluence.class);
	/**
	 * Signal listener. Triggered when battery provide electric energy to a consumer (like propulsion engine or an other battery)
	 */
	private final QueuedSignalAdapter<ConsumeEnergyInfluence> signalProviderListener = new QueuedSignalAdapter<ConsumeEnergyInfluence>(ConsumeEnergyInfluence.class);
	/**
	 * the car where the battery is located
	 */
	private Car car;
			
	public BatteryAgent(Car c) {
		this.energyStored = 0;
		this.car = c;
	}
	
	@Override
	public Status activate(Object... parameters) {
		System.out.println("initialize Battery agent");
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeProposalBatteryCapacityImpl());
		cc.addCapacity(new ComputeEnergyNeededBatteryCapacityImpl());
		cc.addCapacity(new FindBestProposalBatteryCapacityImpl());
		cc.addCapacity(new FindBestRequestCapacityImpl());
		// Get group
		
		GroupAddress ga = getOrCreateGroup(ElectricEnergyExchangeOrganization.class);

		// Add some roles to the group
		if(ga != null) {
			// Send energy stored
			if(requestRole(ElectricEnergyProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
			
			// Received energy 
			//if(requestRole(ElectricEnergyConsumer.class, ga) == null) {
		//		return StatusFactory.cancel(this);
		//	}
		}
		// add signals listener
		this.addSignalListener(this.signalConsumerListener);
		this.addSignalListener(this.signalProviderListener);

		return StatusFactory.ok(this);
	}
	
/*
	@Override
	public Status live() {
		// Signals
		
		Iterator<ProvideEnergyInfluence> cIterator = this.signalConsumerListener.iterator();
		
		while(cIterator.hasNext()) {
			// Send energy
			ProvideEnergyInfluence c = cIterator.next();
			this.energyStored += c.getRequest().getElectricEnergyRequest();
		}
		
		Iterator<ConsumeEnergyInfluence> pInfluence = this.signalProviderListener.iterator();
		
		while(pInfluence.hasNext()) {
			// Stored energy
			ConsumeEnergyInfluence p = pInfluence.next();
			this.energyStored -= p.getRequest().getElectricEnergyRequest();
			System.out.println("signal catched battery - :"+this);
		}
		
		return StatusFactory.ok(this);
	}*/

	/**
	 * @return the energyStored
	 */
	public long getEnergyStored() {
		return this.energyStored;
	}
	
	/**
	 * 
	 * @param energyStored
	 */
	public void setEnergyStored(long energyStored) {
		this.energyStored = energyStored;
	}

	/**
	 * 
	 * @return the car
	 */
	public Car getCar() {
		return car;
	}
	
	@Override
	public String toString() {
		return "BatteryAgent [energyStored=" + energyStored
				+ ", signalConsumerListener=" + signalConsumerListener
				+ ", signalProviderListener=" + signalProviderListener
				+ ", car=" + car + "]";
	}
	
	/****************************************************************/
	/**************************** INNER CLASS ***********************/
	/****************************************************************/
	/**
	 * Inner class, defines a capacity computing electric energy which could be provided
	 * according to a request. Return a proposal.
	 * @author Anthony
	 *
	 */
	private class ComputeProposalBatteryCapacityImpl 
	extends CapacityImplementation 
	implements ComputeProposalCapacity {
		
		public ComputeProposalBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}
		
		@Override
		public void call(CapacityContext call) throws Exception {
			Request request = (Request)call.getInputValues()[0];
			// TODO behavior

			Proposal proposal = new Proposal(request.getElectricEnergyRequest(), request);
			call.setOutputValues(proposal);
			
			System.out.print("ComputeProposalBatteryCapacityImpl, proposal :");
		}
	}
	
	/**
	 * Inner class, defines a capacity computing the electric energy needed.
	 * @author Anthony
	 *
	 */
	private class ComputeEnergyNeededBatteryCapacityImpl
	extends CapacityImplementation
	implements ComputeElectricEnergyNeededCapacity {
		
		/**
		 * Default constructor
		 */
		public ComputeEnergyNeededBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			Request request = new Request(100, Request.Priority.MEDIUM);
			call.setOutputValues(request);
			
			System.out.println("ComputeEnergyNeededBatteryCapacityImpl, request:"+request);
		}
	}
	
	/**
	 * Inner class, defines a capacity finding the best proposal according to a list given
	 * @author Anthony
	 *
	 */
	private class FindBestProposalBatteryCapacityImpl
	extends CapacityImplementation
	implements FindBestProposalCapacity {
		
		public FindBestProposalBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			Proposal best = null;
			
			Object[] o = call.getInputValues();
			
			if(o.length > 0) {
				best = (Proposal)o[0];
			}
			
			if(best != null)
				call.setOutputValues(best);
			System.out.println("FindBestProposalBatteryCapacityImpl, best proposal:"+best);
		}
	}
}
