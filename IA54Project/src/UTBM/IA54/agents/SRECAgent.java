package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeEnergySRECCapacityImpl;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;

public class SRECAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8046981036507670254L;

	private Car car;

	/**
	 * 
	 */
	
	public SRECAgent(Car c) {
		this.car = c;
	}
	
	@Override
	public Status activate(Object... parameters) {
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeEnergySRECCapacityImpl());
		
		GroupAddress ga = getOrCreateGroup(ElectricEnergyExchangeOrganization.class);
	
		if(ga != null) {
			if(requestRole(ElectricEnergyProvider.class, ga) == null) {
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
