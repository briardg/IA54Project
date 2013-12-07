package UTBM.IA54.agents;

import java.util.Iterator;

import org.janusproject.kernel.agent.Agent;
import org.janusproject.kernel.agentsignal.QueuedSignalAdapter;
import org.janusproject.kernel.crio.capacity.CapacityContainer;
import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.status.Status;
import org.janusproject.kernel.status.StatusFactory;

import UTBM.IA54.capacity.ComputeProposalCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacityImpl;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;
import UTBM.IA54.influence.ProvideEnergyInfluence;

public class ThermalEngineAgent extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8950364472045158287L;

	private Car car;
	private double energyProvided;
	private final QueuedSignalAdapter<ProvideEnergyInfluence> signalProviderListener = new QueuedSignalAdapter<ProvideEnergyInfluence>(ProvideEnergyInfluence.class);
	
	public ThermalEngineAgent(Car c) {
		this.car = c;
		this.energyProvided = 0.0;
	}
	
	@Override
	public Status live() {
		Iterator<ProvideEnergyInfluence> pInfluence = this.signalProviderListener.iterator();
		
		while(pInfluence.hasNext()) {
			ProvideEnergyInfluence p = pInfluence.next();
			this.energyProvided -= p.getRequest().getElectricEnergyRequest();
		}
		// add signals listener
		this.addSignalListener(this.signalProviderListener);
		
		return StatusFactory.ok(this);
	}
		
	@Override
	public Status activate(Object... parameters) {
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeProposalTECapacityImpl());
		cc.addCapacity(new FindBestRequestCapacityImpl());
		
		
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
		
	@Override
	public String toString() {
		return "ThermalEngineAgent [energyProvided="
				+ energyProvided + ", signalProviderListener="
				+ signalProviderListener + "]";
	}

	private class ComputeProposalTECapacityImpl 
	extends CapacityImplementation 
	implements ComputeProposalCapacity {
		
		public ComputeProposalTECapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext arg0) throws Exception {
			// TODO Auto-generated method stub
			
		}

	}
}
