package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import utbm.p13.tx52.motor.Engine;
import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacityImpl;
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.capacity.UpdateProviderAttrCapacity;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;

/**
 * Thermal engine agent
 * @author Anthony
 *
 */
public class ThermalEngineAgent extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8950364472045158287L;
	/**
	 * the car
	 */
	private Car car;
	private double energyProvided;
	private Engine engine;
	
	/**
	 * 
	 * @param c the car
	 */
	public ThermalEngineAgent(Car c, Engine e) {
		this.car = c;
		this.engine = e;
		this.energyProvided = 0.0;
	}
		
	@Override
	public Status activate(Object... parameters) {
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeProposalTECapacityImpl());
		cc.addCapacity(new FindBestRequestCapacityImpl());
		cc.addCapacity(new UpdateProviderAttrTECapacityImpl());
				
		GroupAddress ga = getOrCreateGroup(ElectricEnergyExchangeOrganization.class);
		// Associate role
		if(ga != null) {
			if(requestRole(ElectricEnergyProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
		}
		return StatusFactory.ok(this);
	}

	/**
	 * 
	 * @return the car
	 */
	public Car getCar() {
		return car;
	}
			
	/**
	 * 
	 * @return energy provided
	 */
	public double getEnergyProvided() {
		return energyProvided;
	}

	/**
	 * 
	 * @param energyProvided energy provided
	 */
	public void setEnergyProvided(double energyProvided) {
		this.energyProvided = energyProvided;
	}

	@Override
	public String toString() {
		return "ThermalEngineAgent [energyProvided="
				+ energyProvided + ", signalProviderListener=]";
	}


	/****************************************************************/
	/**************************** INNER CLASS ***********************/
	/****************************************************************/
	/**
	 * Inner class,  defines how to compute a Proposal according to a request
	 * @author Anthony
	 *
	 */
	private class ComputeProposalTECapacityImpl 
	extends CapacityImplementation 
	implements ComputeProposalCapacity {
		
		/**
		 * 
		 */
		public ComputeProposalTECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior done with optimalPower could change with specifics values
			
			Request request = (Request)call.getInputValues()[0];
			
			Proposal proposal = new Proposal(ThermalEngineAgent.this.engine.getOptimalPower(), request);
			call.setOutputValues(proposal);
		}
	}
	
	/**
	 * Inner class, update some attributes of the agent
	 * @author Anthony
	 *
	 */
	private class UpdateProviderAttrTECapacityImpl 
	extends CapacityImplementation 
	implements UpdateProviderAttrCapacity {
		/**
		 * 
		 */
		public UpdateProviderAttrTECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			// TODO behavior done just by updated tank
			
			//Proposal p = (Proposal)call.getInputValues()[0];
			//ThermalEngineAgent.this.setEnergyProvided(ThermalEngineAgent.this.getEnergyProvided()-p.getElectricEnergyProposal());
			ThermalEngineAgent.this.engine.updateTank();
			System.out.println(ThermalEngineAgent.this.getName()+" energy : "+ThermalEngineAgent.this.getEnergyProvided());
		}
	}
}
