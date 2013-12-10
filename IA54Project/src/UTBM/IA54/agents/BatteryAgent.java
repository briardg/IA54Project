package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.ComputeRequestCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacityImpl;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.capacity.UpdateProviderAttrCapacity;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyConsumer;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;

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
	private double energyStored;
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
		System.out.println("initialize "+this.getName()+" agent");
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeProposalBatteryCapacityImpl());
		cc.addCapacity(new ComputeRequestBatteryCapacityImpl());
		cc.addCapacity(new FindBestProposalBatteryCapacityImpl());
		cc.addCapacity(new FindBestRequestCapacityImpl());
		cc.addCapacity(new UpdateProviderAttrBatteryCapacityImpl());
		
		// Get group		
		GroupAddress ga = getOrCreateGroup(ElectricEnergyExchangeOrganization.class);

		// Add some roles to the group
		if(ga != null) {
			// Send energy stored
			if(requestRole(ElectricEnergyProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
			
			// Received energy 
			if(requestRole(ElectricEnergyConsumer.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
		}
		
		return StatusFactory.ok(this);
	}
	
	/**
	 * @return the energyStored
	 */
	public synchronized double getEnergyStored() {
		return this.energyStored;
	}
	
	/**
	 * 
	 * @param energyStored
	 */
	public synchronized void setEnergyStored(double energyStored) {
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
				+ ", signalConsumerListener=" + ", signalProviderListener="
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
			
			System.out.print("ComputeProposalBatteryCapacityImpl, "+BatteryAgent.this.getName()+", proposal :");
		}
	}
	
	/**
	 * Inner class, defines a capacity computing the electric energy needed.
	 * @author Anthony
	 *
	 */
	private class ComputeRequestBatteryCapacityImpl
	extends CapacityImplementation
	implements ComputeRequestCapacity {
		
		/**
		 * Default constructor
		 */
		public ComputeRequestBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			Request request = new Request(10, Request.Priority.MEDIUM);
			call.setOutputValues(request);
						
			System.out.println("ComputeRequestBatteryCapacityImpl, "+BatteryAgent.this.getName()+", request:"+request);
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
			BatteryAgent.this.setEnergyStored(BatteryAgent.this.getEnergyStored()+best.getElectricEnergyProposal());
			System.out.println("FindBestProposalBatteryCapacityImpl, "+BatteryAgent.this.getName()+", best proposal:"+best);
			System.out.println("agent "+BatteryAgent.this.getName()+" : "+BatteryAgent.this.getEnergyStored());
		}
	}

	private class UpdateProviderAttrBatteryCapacityImpl 
	extends CapacityImplementation 
	implements UpdateProviderAttrCapacity {
		
		public UpdateProviderAttrBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			Request r = (Request)call.getInputValues()[0];
			BatteryAgent.this.setEnergyStored(BatteryAgent.this.getEnergyStored()-r.getElectricEnergyRequest());
			System.out.println(BatteryAgent.this.getName()+" : "+BatteryAgent.this.getEnergyStored());
		}
	}
}
