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

import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacity;
import UTBM.IA54.capacity.Proposal;
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
			
			// TODO Behavior done without real object
			
			Request request = (Request)call.getInputValues()[0];
			double v = request.getElectricEnergyRequest();
			if(v > SRECAgent.this.energyProvided)
				v = SRECAgent.this.energyProvided;
			
			Proposal proposal = new Proposal(v, request);
			call.setOutputValues(proposal);
		}
	}
	
	/**
	 * Implementation of {@link FindBestRequestCapacity}. Defines how find the best request from a list
	 * of requests
	 * @author Anthony
	 *
	 */
	public class FindBestRequestCapacityImpl
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
					}else if(Math.abs(r.getPosition()-SRECAgent.this.car.getPosition()) < Math.abs(bestr.getPosition()-SRECAgent.this.car.getPosition())){
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
			// TODO Behavior done without real object
			
			Proposal p = (Proposal)call.getInputValues()[0];
			double v = SRECAgent.this.getEnergyProvided() - p.getElectricEnergyProposal();
			
			SRECAgent.this.setEnergyProvided(v);
			System.out.println(SRECAgent.this.getName()+" energy : "+SRECAgent.this.getEnergyProvided());
		}
	}
}
