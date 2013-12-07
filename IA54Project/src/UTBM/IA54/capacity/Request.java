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
	private Priority priority;
	
	public Request(RoleAddress roleConsumer, double energyQuantity, Priority requestPriority) {
		this.consumer = roleConsumer;
		this.electricEnergyRequest = energyQuantity;
		this.date = new Date();
		this.priority = requestPriority;
	}
	
	public Request(double energyQuantity, Priority requestPriority) {
		this.consumer = null;
		this.electricEnergyRequest = energyQuantity;
		this.date = new Date();
		this.priority = requestPriority;
	}
	
	public void setConsumer(RoleAddress consumerAddress) {
		this.consumer = consumerAddress;
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

	public Priority getPriority() {
		return priority;
	}	
	
	@Override
	public String toString() {
		return "Request [electricEnergyRequest=" + electricEnergyRequest
				+ ", consumer=" + consumer + ", date=" + date + ", priority="
				+ priority + "]";
	}

	public enum Priority {
		VERY_LOW,
		LOW,
		MEDIUM,
		HIGH,
		VERY_HIGH		
	}
}
