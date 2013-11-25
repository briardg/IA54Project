package UTBM.IA54.exchangeEnergy;

import org.janusproject.kernel.crio.core.Role;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

public class ProduceTorqueRole extends Role {
	
	private State state = null;

	public ProduceTorqueRole() {

	}

	@Override
	public Status activate(Object... params) {
		this.state = State.WAIT_ENERGY;
		
		return StatusFactory.ok(this);
	}


	@Override
	public Status live() {
		this.state = this.run();
		
		return StatusFactory.ok(this);
	}
	
	private State run() {
		switch(this.state) {
		case WAIT_ENERGY:
			
			return State.PRODUCE_TORQUE;
			
		case PRODUCE_TORQUE:
			
			return State.WAIT_ENERGY;
		default:
			return this.state;
		}
	}

	private enum State {
		WAIT_ENERGY,
		PRODUCE_TORQUE;
	}
}
