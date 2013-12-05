package UTBM.IA54.capacity;

import java.util.Date;

import org.janusproject.kernel.crio.core.RoleAddress;

public class Request {
	
	private double electricEnergyRequest;
	private RoleAddress consumer;
	private Date date;
	/**
	 * 1 to 5
	 * 1 => low priority
	 * 5 => high priority
	 */
	private int priority;
	
	public Request(RoleAddress roleConsumer, double energyQuantity, int requestPriority) {
		this.consumer = roleConsumer;
		this.electricEnergyRequest = energyQuantity;
		this.date = new Date();
		this.priority = requestPriority;
	}

	public double getElectricEnergyRequest() {
		return this.electricEnergyRequest;
	}

	public RoleAddress getConsumer() {
		return this.consumer;
	}
	
	public Date getIdentifyDate() {
		return this.date;
	}

	public int getPriority() {
		return priority;
	}	
}
