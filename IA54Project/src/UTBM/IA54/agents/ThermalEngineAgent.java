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
import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.capacity.UpdateProviderAttrCapacity;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;

public class ThermalEngineAgent extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8950364472045158287L;

	private Car car;
	private double energyProvided;
	
	public ThermalEngineAgent(Car c) {
		this.car = c;
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
		return "ThermalEngineAgent [energyProvided="
				+ energyProvided + ", signalProviderListener=]";
	}

	private class ComputeProposalTECapacityImpl 
	extends CapacityImplementation 
	implements ComputeProposalCapacity {
		
		public ComputeProposalTECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			Request request = (Request)call.getInputValues()[0];
			// TODO behavior
			
			Proposal proposal = new Proposal(request.getElectricEnergyRequest(), request);
			call.setOutputValues(proposal);
			
			System.out.print("ComputeProposalTECapacityImpl, "+ThermalEngineAgent.this.getName()+", proposal :");
		}
	}
	
	private class UpdateProviderAttrTECapacityImpl 
	extends CapacityImplementation 
	implements UpdateProviderAttrCapacity {
		
		public UpdateProviderAttrTECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			Request r = (Request)call.getInputValues()[0];
			ThermalEngineAgent.this.setEnergyProvided(ThermalEngineAgent.this.getEnergyProvided()-r.getElectricEnergyRequest());
			System.out.println(ThermalEngineAgent.this.getName()+" : "+ThermalEngineAgent.this.getEnergyProvided());
		}
	}
}
