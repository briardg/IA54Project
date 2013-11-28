package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeEnergyBatteryCapacityImpl;
import UTBM.IA54.capacity.StoreElectricEnergyCapacityImpl;
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
		cc.addCapacity(new StoreElectricEnergyCapacityImpl());
		
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
	
	@Override
	public Status live() {
		return super.live();
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
}
