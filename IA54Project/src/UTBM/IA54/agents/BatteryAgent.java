package UTBM.IA54.agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import utbm.p13.tx52.battery.AbstractBattery;
import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.ComputeRequestCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacityImpl;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.capacity.Request.Priority;
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
	
	private AbstractBattery battery;
			
	public BatteryAgent(Car c, AbstractBattery b) {
		this.energyStored = 0;
		this.car = c;
		this.battery = b;
	}
	
	@Override
	public Status activate(Object... parameters) {
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
			if(requestRole(ElectricEnergyProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
			
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
		
		/**
		 * 
		 */
		public ComputeProposalBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}
		
		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior done
			
			Request request = (Request)call.getInputValues()[0];
			BatteryAgent.this.energyStored = BatteryAgent.this.battery.getMaxDisChargePower();
			
			if(request.getElectricEnergyRequest() < BatteryAgent.this.energyStored)
				BatteryAgent.this.energyStored = request.getElectricEnergyRequest();
			
			Proposal proposal = new Proposal(BatteryAgent.this.energyStored, request);
			call.setOutputValues(proposal);
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
		 * 
		 */
		public ComputeRequestBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior done
			Priority p = Request.Priority.VERY_LOW;
			if(BatteryAgent.this.battery.getPourcentageOfCharge()<20)
				p=Request.Priority.VERY_HIGH;
			else if(BatteryAgent.this.battery.getPourcentageOfCharge()<40)
				p=Request.Priority.HIGH;
			else if(BatteryAgent.this.battery.getPourcentageOfCharge()<60)
				p=Request.Priority.MEDIUM;
			else if(BatteryAgent.this.battery.getPourcentageOfCharge()<80)
				p=Request.Priority.LOW;
			
			Request request = new Request(BatteryAgent.this.battery.getMaxChargePower(), p);
			call.setOutputValues(request);
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
		
		/**
		 * 
		 */
		public FindBestProposalBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior done
			
			ArrayList<Proposal> best = new ArrayList<Proposal>();

			ArrayList<Proposal> proposalList = (ArrayList<Proposal>) Arrays.asList((Proposal[])call.getInputValues());
			 
			Collections.sort(proposalList, new Comparator<Proposal>() {

				@Override
				public int compare(Proposal p1, Proposal p2) {
					
					if(p1.getElectricEnergyProposal() > p2.getElectricEnergyProposal())
						return -1;
					else if(p1.getElectricEnergyProposal() == p2.getElectricEnergyProposal())
						return 0;
					else
						return 1;
				}				
			});
						
			double value = 0;
			for(Proposal p : proposalList){
				if(p.getElectricEnergyProposal()+value<=p.getRequest().getElectricEnergyRequest()){
					value += p.getElectricEnergyProposal();
					best.add(p);
				}
			}
			
			if(best != null)
				call.setOutputValues(best);
			
			BatteryAgent.this.battery.setCurrentCapacityByCharging(value);
			
			System.out.println(BatteryAgent.this.getName()+" consumer : energy "+BatteryAgent.this.getEnergyStored()+", proposal accepted : "+best);
		}
	}

	/**
	 * Inner class, update some attributes of the agent
	 * @author Anthony
	 *
	 */
	private class UpdateProviderAttrBatteryCapacityImpl 
	extends CapacityImplementation 
	implements UpdateProviderAttrCapacity {
		
		/**
		 * 
		 */
		public UpdateProviderAttrBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior done
			
			//Proposal p = (Proposal)call.getInputValues()[0];
			//BatteryAgent.this.setEnergyStored(BatteryAgent.this.getEnergyStored() - p.getElectricEnergyProposal());
			BatteryAgent.this.battery.setCurrentCapacityByUsing(BatteryAgent.this.energyStored);
			BatteryAgent.this.energyStored = 0;
			System.out.println(BatteryAgent.this.getName()+"provider : "+BatteryAgent.this.getEnergyStored());
		}
	}
}
