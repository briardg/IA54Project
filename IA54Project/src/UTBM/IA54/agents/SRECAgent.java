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

public class SRECAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8046981036507670254L;

	private double energyProvided;
	private Car car;
	
	/**
	 * 
	 * @param c
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
	
		if(ga != null) {
			if(requestRole(ElectricEnergyProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
		}
		
		return StatusFactory.ok(this);
	}
	
	public Car getCar() {
		return car;
	}
			
	public double getEnergyProvided() {
		return energyProvided;
	}

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
	private class ComputeProposalSRECCapacityImpl 
	extends CapacityImplementation 
	implements ComputeProposalCapacity {
		
		public ComputeProposalSRECCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			call.setOutputValues(new Request(100, Request.Priority.MEDIUM));
		}
	}
	
	private class UpdateProviderAttrSRECCapacityImpl 
	extends CapacityImplementation 
	implements UpdateProviderAttrCapacity {
		
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
