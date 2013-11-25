package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeTorque;
import UTBM.IA54.capacity.StoreEnergy;
import UTBM.IA54.exchangeEnergy.ExchangeEnergyOrganization;
import UTBM.IA54.exchangeEnergy.ProduceTorqueRole;
import UTBM.IA54.exchangeEnergy.ReceiveEnergyRole;

public class PropulsionEngineAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8957085011652833189L;

	public PropulsionEngineAgent() {

	}
	
	@Override
	public Status activate(Object... parameters) {
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeTorque());
		cc.addCapacity(new StoreEnergy());
		
		GroupAddress ga = createGroup(ExchangeEnergyOrganization.class);
	
		if(ga != null) {
			if(requestRole(ProduceTorqueRole.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
			
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
}
