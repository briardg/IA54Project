package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeEnergySREC;
import UTBM.IA54.exchangeEnergy.ExchangeEnergyOrganization;
import UTBM.IA54.exchangeEnergy.SendEnergyRole;

public class SRECAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8046981036507670254L;

	/**
	 * 
	 */
	
	public SRECAgent() {

	}
	
	@Override
	public Status activate(Object... parameters) {
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeEnergySREC());
		
		GroupAddress ga = createGroup(ExchangeEnergyOrganization.class);
	
		if(ga != null) {
			if(requestRole(SendEnergyRole.class, ga) == null) {
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
}
