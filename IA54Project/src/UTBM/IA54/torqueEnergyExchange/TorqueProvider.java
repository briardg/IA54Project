package UTBM.IA54.torqueEnergyExchange;

import org.janusproject.kernel.crio.core.HasAllRequiredCapacitiesCondition;
import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeTorqueCapacity;

/**
 * Torque provider
 * @author Anthony et Gautier
 *
 */
public class TorqueProvider extends Role {
	/**
	 * State of the role
	 */
	private State state = null;

	/**
	 * 
	 */
	public TorqueProvider() {
		addObtainCondition(new HasAllRequiredCapacitiesCondition(ComputeTorqueCapacity.class));
	}

	@Override
	public Status activate(Object... params) {
		this.state = State.PRODUCE_TORQUE;
		
		return StatusFactory.ok(this);
	}


	@Override
	public Status live() {
		this.state = this.run();
		
		return StatusFactory.ok(this);
	}
	
	private State run() {
		switch(this.state) {
		case PRODUCE_TORQUE:
			try {
				this.executeCapacityCall(ComputeTorqueCapacity.class);				
			} catch (Throwable e) {
				error(e.getLocalizedMessage());
				return State.PRODUCE_TORQUE;
			}
			return State.PRODUCE_TORQUE;
		default:
			return this.state;
		}
	}

	/**
	 * Enum which defines states of the role
	 * @author Anthony et Gautier
	 *
	 */
	private enum State {
		WAIT_ENERGY,
		PRODUCE_TORQUE;
	}
}
