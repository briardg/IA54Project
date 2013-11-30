package UTBM.IA54.capacity;

import org.janusproject.kernel.crio.core.RoleAddress;

public class Request {
	
	private double electricEnergyRequest;
	private RoleAddress consumer;
	
	public Request(RoleAddress roleConsumer, double energyProposal) {
		this.consumer = roleConsumer;
		this.electricEnergyRequest = energyProposal;
	}

	public double getElectricEnergyRequest() {
		return this.electricEnergyRequest;
	}

	public RoleAddress getConsumer() {
		return this.consumer;
	}
}
