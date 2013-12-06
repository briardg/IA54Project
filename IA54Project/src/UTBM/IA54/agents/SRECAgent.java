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

import UTBM.IA54.capacity.ComputeElectricEnergyProvidedCapacity;
import UTBM.IA54.capacity.FindBestRequestCapacityImpl;
import UTBM.IA54.capacity.Request;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyExchangeOrganization;
import UTBM.IA54.electricEnergyExchange.ElectricEnergyProvider;
import UTBM.IA54.energyManager.Car;
import UTBM.IA54.influence.ProvideEnergyInfluence;

public class SRECAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8046981036507670254L;

	private double energyProvided;
	private Car car;
	private final QueuedSignalAdapter<ProvideEnergyInfluence> signalProviderListener = new QueuedSignalAdapter<ProvideEnergyInfluence>(ProvideEnergyInfluence.class);
	
	/**
	 * 
	 */
	
	public SRECAgent(Car c) {
		this.energyProvided = 0.0;
		this.car = c;
	}
	
	@Override
	public Status activate(Object... parameters) {		
		// Initialize Capacity
		CapacityContainer cc = getCapacityContainer();
		cc.addCapacity(new ComputeEnergySRECCapacityImpl());
		cc.addCapacity(new FindBestRequestCapacityImpl());
		
		GroupAddress ga = getOrCreateGroup(ElectricEnergyExchangeOrganization.class);
	
		if(ga != null) {
			if(requestRole(ElectricEnergyProvider.class, ga) == null) {
				return StatusFactory.cancel(this);
			}
		}
		// add signals listener
		this.addSignalListener(this.signalProviderListener);
		
		return StatusFactory.ok(this);
	}

	@Override
	public Status live() {		
		Iterator<ProvideEnergyInfluence> pInfluence = this.signalProviderListener.iterator();
		
		while(pInfluence.hasNext()) {
			ProvideEnergyInfluence p = pInfluence.next();
			this.energyProvided -= p.getRequest().getElectricEnergyRequest();
		}
		
		return StatusFactory.ok(this);
	}
	
	public Car getCar() {
		return car;
	}
	
	private class ComputeEnergySRECCapacityImpl 
	extends CapacityImplementation 
	implements ComputeElectricEnergyProvidedCapacity {
		
		public ComputeEnergySRECCapacityImpl() {
			super(CapacityImplementationType.DIRECT_ACTOMIC);
		}

		@Override
		public void call(CapacityContext call) throws Exception {
			call.setOutputValues(new Request(100, Request.Priority.MEDIUM));
		}
	}
}
