package UTBM.IA54.agents;

import java.util.ArrayList;
import java.util.List;

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
import UTBM.IA54.capacity.FindBestRequestCapacity;
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
	public ThermalEngineAgent(Car c) {
		this.car = c;
		this.engine = c.getV().getEngine();
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
			
			Request request = (Request)call.getInputValues()[0];
			
			//get OptimalPower only for the moment
			Proposal proposal = new Proposal(ThermalEngineAgent.this.engine.getOptimalPower(), request);
			call.setOutputValues(proposal);
		}
	}
	
	/**
	 * Implementation of {@link FindBestRequestCapacity}. Defines how find the best request from a list
	 * of requests
	 * @author Anthony
	 *
	 */
	private class FindBestRequestCapacityImpl
	extends CapacityImplementation
	implements FindBestRequestCapacity {
		
		public FindBestRequestCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			
			ArrayList<Request> requests = (ArrayList<Request>)call.getInputValues()[0];	
			List<Request> bestrlist = new ArrayList<>();
			Request bestr = null;
			
			//get the request with the higher priority
			for(Request r : requests){
				if(bestr == null){
					bestr = r;
				}else if(r.getPriority().ordinal() > bestr.getPriority().ordinal()){
						bestr = r;
				}
			}
			
			//get all request with the same priority than the best
			for(Request r : requests){
				if(bestr.getPriority().ordinal() == r.getPriority().ordinal()){
					bestrlist.add(r);
				}
			}
			
			//if list more than 1 get the closest best request
			if(bestrlist.size()>1){
				bestr=null;
				for(Request r : bestrlist){
					if(bestr == null){
						bestr = r;
					}else if(Math.abs(r.getPosition()-ThermalEngineAgent.this.car.getPosition()) < Math.abs(bestr.getPosition()-ThermalEngineAgent.this.car.getPosition())){
							bestr = r;
					}
				}
			// else get the best request
			}else{
				bestr=bestrlist.get(1);
			}
			
			//return best request
			call.setOutputValues(bestr);
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
			
			
			//Proposal p = (Proposal)call.getInputValues()[0];
			//ThermalEngineAgent.this.setEnergyProvided(ThermalEngineAgent.this.getEnergyProvided()-p.getElectricEnergyProposal());
			
			//just need toUpdate the Tank from the optimalPower
			ThermalEngineAgent.this.engine.updateTank();
			System.out.println(ThermalEngineAgent.this.getName()+" energy : "+ThermalEngineAgent.this.getEnergyProvided());
		}
	}
}
