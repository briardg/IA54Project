package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeTorqueCapacityImpl;
import UTBM.IA54.capacity.StoreElectricEnergyCapacityImpl;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyConsumer;
import UTBM.IA54.energyManager.Car;
import UTBM.IA54.torque.TorqueProvider;

public class PropulsionEngineAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8957085011652833189L;

	private Car car;

	public PropulsionEngineAgent(Car c) {
		this.car = c;
	}
	
	@Override
	public Status activate(Object... parameters) {
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeTorqueCapacityImpl());
		cc.addCapacity(new StoreElectricEnergyCapacityImpl());
		
		GroupAddress ga = getOrCreateGroup(ElectricEnergyExchangeOrganization.class);
	
		if(ga != null) {
			if(requestRole(TorqueProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
			
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

	public Car getCar() {
		return car;
	}
}
