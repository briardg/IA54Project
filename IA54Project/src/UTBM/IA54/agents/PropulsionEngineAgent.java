package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeTorqueCapacity;
import UTBM.IA54.capacity.ComputeEnergyNeededCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
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
		cc.addCapacity(new ComputeEnergyNeededPECapacityImpl());
		cc.addCapacity(new FindBestProposalPECapacityImpl());
		
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

	public Car getCar() {
		return car;
	}
	
	private class FindBestProposalPECapacityImpl
	extends CapacityImplementation
	implements FindBestProposalCapacity {
		
		public FindBestProposalPECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext arg0) throws Exception {
			// TODO Auto-generated method stub
			arg0.getCaller().getAddress();
		}
	}
	
	private class ComputeEnergyNeededPECapacityImpl
	extends CapacityImplementation
	implements ComputeEnergyNeededCapacity {

		public ComputeEnergyNeededPECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}
		
		@Override
		public void call(CapacityContext arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
	
	private class ComputeTorqueCapacityImpl 
	extends CapacityImplementation
	implements ComputeTorqueCapacity {
		
		public ComputeTorqueCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}
	}
}
