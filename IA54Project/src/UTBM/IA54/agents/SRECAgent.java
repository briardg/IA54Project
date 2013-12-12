package UTBM.IA54.agents;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacityImpl;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.capacity.UpdateProviderAttrCapacity;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;

/**
 * SREC Agent
 * @author Anthony
 *
 */
public class SRECAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8046981036507670254L;
	
	private double energyProvided;
	/**
	 * The car
	 */
	private Car car;
	
	/**
	 * 
	 * @param c a car
	 */
	public SRECAgent(Car c) {
		this.energyProvided = 0.0;
		this.car = c;
	}
	
	@Override
	public Status activate(Object... parameters) {	
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeProposalSRECCapacityImpl());
		cc.addCapacity(new FindBestRequestCapacityImpl());
		cc.addCapacity(new UpdateProviderAttrSRECCapacityImpl());
		
		GroupAddress ga = getOrCreateGroup(ElectricEnergyExchangeOrganization.class);
	
		// associate Role
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
		return "SRECAgent [energyProvided=" + energyProvided
				+ ", signalProviderListener=]";
	}

	/****************************************************************/
	/**************************** INNER CLASS ***********************/
	/****************************************************************/
	/**
	 * Inner class, defines how to compute a Proposal according to a Request
	 * @author Anthony
	 *
	 */
	private class ComputeProposalSRECCapacityImpl 
	extends CapacityImplementation 
	implements ComputeProposalCapacity {
		
		/**
		 * 
		 */
		public ComputeProposalSRECCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			call.setOutputValues(new Request(100, Request.Priority.MEDIUM));
		}
	}
	
	/**
	 * Inner class, Update some attributes of the agent
	 * @author Anthony
	 *
	 */
	private class UpdateProviderAttrSRECCapacityImpl 
	extends CapacityImplementation 
	implements UpdateProviderAttrCapacity {
		
		/**
		 * 
		 */
		public UpdateProviderAttrSRECCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			Request r = (Request)call.getInputValues()[0];
			SRECAgent.this.setEnergyProvided(SRECAgent.this.getEnergyProvided()-r.getElectricEnergyRequest());
			System.out.println(SRECAgent.this.getName()+" : "+SRECAgent.this.getEnergyProvided());
		}
	}
}
