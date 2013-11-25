package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeEnergyBattery;
import UTBM.IA54.capacity.StoreEnergy;
import UTBM.IA54.exchangeEnergy.ExchangeEnergyOrganization;
import UTBM.IA54.exchangeEnergy.ReceiveEnergyRole;
import UTBM.IA54.exchangeEnergy.SendEnergyRole;

public class BatteryAgent extends Agent{

	private long energyStored;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7646489766352232138L;
		
	public BatteryAgent() {
		this.energyStored = 0;
	}
	
	@Override
	public Status activate(Object... parameters) {
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeEnergyBattery());
		cc.addCapacity(new StoreEnergy());
		
		GroupAddress ga = createGroup(ExchangeEnergyOrganization.class);
	
		if(ga != null) {
			// Send energy stored
			if(requestRole(SendEnergyRole.class, ga, parameters) == null) {
				return StatusFactory.cancel(this);
			}
			
			// Received energy produced
			if(requestRole(ReceiveEnergyRole.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
		}
		return StatusFactory.ok(this);
	}
	
	@Override
	public Status live() {
		// TODO Auto-generated method stub
		return super.live();
	}

	/**
	 * @return the energyStored
	 */
	public long getEnergyStored() {
		return this.energyStored;
	}
}
