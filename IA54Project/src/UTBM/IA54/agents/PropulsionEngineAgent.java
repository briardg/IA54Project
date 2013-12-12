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
import UTBM.IA54.capacity.ComputeRequestCapacity;
import UTBM.IA54.capacity.FindBestProposalCapacity;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyConsumer;
import UTBM.IA54.energyManager.Car;

/**
 * Propulsion engine agent
 * @author Anthony
 *
 */
public class PropulsionEngineAgent extends Agent {

	/**
	 * UID
	 */
	private static final long serialVersionUID = -8957085011652833189L;
	/**
	 * the car
	 */
	private Car car;
	/**
	 * energy consume by propulsion engine
	 */
	private double energyConsumed;
	/**
	 * Torque provided according to the energyConsume
	 */
	private double torqueProvided;
	
	/**
	 * Default constructor
	 * @param c car
	 */
	public PropulsionEngineAgent(Car c) {
		this.car = c;
	}
	
	@Override
	public Status activate(Object... parameters) {
		this.energyConsumed = 0.0;		
		
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeTorqueCapacityImpl());
		cc.addCapacity(new ComputeRequestPECapacityImpl());
		cc.addCapacity(new FindBestProposalPECapacityImpl());
		// get group

		GroupAddress ga = this.getOrCreateGroup(ElectricEnergyExchangeOrganization.class);
	
		// add some roles
		if(ga != null) {
			/*if(requestRole(TorqueProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}*/
			
			if(requestRole(ElectricEnergyConsumer.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
		}
		
		return StatusFactory.ok(this);
	}

	/****************************/
	/**** Getter, setter ********/
	/****************************/
	/**
	 * 
	 * @return energy consumed
	 */
	public synchronized double getEnergyConsume() {
		return energyConsumed;
	}

	/**
	 * 
	 * @param energyConsume
	 */
	public synchronized void setEnergyConsume(double energyConsume) {
		this.energyConsumed = energyConsume;
	}

	/**
	 * 
	 * @return torque provided
	 */
	public synchronized double getTorqueProvided() {
		return torqueProvided;
	}

	/**
	 * 
	 * @param torqueProvided
	 */
	public synchronized void setTorqueProvided(double torqueProvided) {
		this.torqueProvided = torqueProvided;
	}
	
	/**
	 * 
	 * @return car
	 */
	public Car getCar() {
		return car;
	}
	/****************************/
	/*** End Getter, setter *****/
	/****************************/
	
	@Override
	public String toString() {
		return "PropulsionEngineAgent [energyConsumed="
				+ energyConsumed + ", torqueProvided=" + torqueProvided + "]";
	}


	/****************************************************************/
	/**************************** INNER CLASS ***********************/
	/****************************************************************/
	/**
	 * Inner class, defines a capacity finding the best proposal
	 * according to a list
	 * @author Anthony
	 *
	 */
	private class FindBestProposalPECapacityImpl
	extends CapacityImplementation
	implements FindBestProposalCapacity {
		
		public FindBestProposalPECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior
			
			Proposal best = null;
			
			Object[] o = call.getInputValues();
			
			if(o.length > 0) {
				best = (Proposal)o[0];
			}
			
			if(best != null)
				call.setOutputValues(best);
			
			PropulsionEngineAgent.this.setEnergyConsume(PropulsionEngineAgent.this.getEnergyConsume()+best.getElectricEnergyProposal());
			System.out.println(PropulsionEngineAgent.this.getName()+" : "+PropulsionEngineAgent.this.getEnergyConsume());
		}
	}
	
	/**
	 * Inner class, defines a capacity computing the electric energy needed 
	 * by the agent
	 * @author Anthony
	 *
	 */
	private class ComputeRequestPECapacityImpl
	extends CapacityImplementation
	implements ComputeRequestCapacity {

		public ComputeRequestPECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}
		
		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior
			
			// create a Request according to the needed of energy
			Request request = new Request(10, Request.Priority.MEDIUM);
			call.setOutputValues(request);
		}
	}
	
	/**
	 * Inner class, defines a capacity computing the torque provided
	 * @author Anthony
	 *
	 */
	private class ComputeTorqueCapacityImpl 
	extends CapacityImplementation
	implements ComputeTorqueCapacity {
		
		public ComputeTorqueCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior
			
			PropulsionEngineAgent.this.setTorqueProvided(PropulsionEngineAgent.this.getEnergyConsume()*0.5);
			PropulsionEngineAgent.this.setEnergyConsume(0.0);
			
			System.out.println("ComputeTorqueCapacityImpl, "+PropulsionEngineAgent.this.getName()+" : "+PropulsionEngineAgent.this.getTorqueProvided());
		}
	}
}
