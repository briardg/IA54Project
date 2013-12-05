package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeElectricEnergyCapacity;
import UTBM.IA54.capacity.ComputeEnergyNeededCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyConsumer;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;

public class BatteryAgent extends Agent{

	private long energyStored;
	
	private Car car;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7646489766352232138L;
		
	public BatteryAgent(Car c) {
		this.energyStored = 0;
		this.car = c;
	}
	
	@Override
	public Status activate(Object... parameters) {
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeEnergyBatteryCapacityImpl());
		cc.addCapacity(new ComputeEnergyNeededBatteryCapacityImpl());
		cc.addCapacity(new FindBestProposalBatteryCapacityImpl());
		
		GroupAddress ga = getOrCreateGroup(ElectricEnergyExchangeOrganization.class);
		
		if(ga != null) {
			// Send energy stored
			if(requestRole(ElectricEnergyProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
			
			// Received energy produced
			if(requestRole(ElectricEnergyConsumer.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
		}
		return StatusFactory.ok(this);
	}

	/**
	 * @return the energyStored
	 */
	public long getEnergyStored() {
		return this.energyStored;
	}

	public Car getCar() {
		return car;
	}
	
	private class ComputeEnergyBatteryCapacityImpl 
	extends CapacityImplementation 
	implements ComputeElectricEnergyCapacity {
		
		public ComputeEnergyBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}
		
		@Override
		public void call(CapacityContext arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class ComputeEnergyNeededBatteryCapacityImpl
	extends CapacityImplementation
	implements ComputeEnergyNeededCapacity {
		
		public ComputeEnergyNeededBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext arg0) throws Exception {
			// TODO Auto-generated method stub
			BatteryAgent.this.getCar();
		}
	}
	
	private class FindBestProposalBatteryCapacityImpl
	extends CapacityImplementation
	implements FindBestProposalCapacity {
		
		public FindBestProposalBatteryCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext arg0) throws Exception {
			// TODO Auto-generated method stub
			arg0.getCaller().getAddress();
		}
	}
}
