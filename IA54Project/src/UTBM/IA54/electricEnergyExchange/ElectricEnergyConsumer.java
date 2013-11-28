package UTBM.IA54.electricEnergyExchange;

import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.StoreElectricEnergyCapacityImpl;

public class ElectricEnergyConsumer extends Role {
	
	private State state = null;

	public ElectricEnergyConsumer() {
		addObtainCondition(new HasAllRequiredCapacitiesCondition(StoreElectricEnergyCapacityImpl.class));
	}

	@Override
	public Status activate(Object... params) {
		this.state = State.WAITING_CALLER;
		
		return StatusFactory.ok(this);
	}


	@Override
	public Status live() {
		this.state = this.run();
		
		return StatusFactory.ok(this);
	}
	
	private State run() {
		switch(this.state) {
		case WAITING_CALLER:
			
			return State.RECEIVE_ENERGY;
			
		case RECEIVE_ENERGY:
			
			return State.WAITING_CALLER;
		default:
			return this.state;
		}
	}

	private enum State {
		WAITING_CALLER,
		RECEIVE_ENERGY;
	}
}
