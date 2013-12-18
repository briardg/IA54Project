package UTBM.IA54.capacity;

import java.util.Date;

import org.janusproject.kernel.crio.core.RoleAddress;

/**
 * A Request is sent by consumer to ask  proposals from providers
 * @author Anthony
 *
 */
public class Request {
	/**
	 * quantity of energy needed
	 */
	private double electricEnergyRequest;
	/**
	 * the RoleAddress of the creator of the request
	 */
	private RoleAddress consumer;
	/**
	 * the creation date
	 */
	private Date date;
	/**
	 * the priority of the request
	 */
	private Priority priority;
	
	/**
	 * 
	 * @param roleConsumer
	 * @param energyQuantity
	 * @param requestPriority
	 */
	public Request(RoleAddress roleConsumer, double energyQuantity, Priority requestPriority) {
		this.consumer = roleConsumer;
		this.electricEnergyRequest = energyQuantity;
		this.date = new Date();
		this.priority = requestPriority;
	}
	
	/**
	 * 
	 * @param energyQuantity
	 * @param requestPriority
	 */
	public Request(double energyQuantity, Priority requestPriority) {
		this(null,energyQuantity,requestPriority);
	}
	

	/**************************************/
	/********** Getter and Setter *********/
	/**************************************/
	/**
	 * 
	 * @param consumerAddress the RoleAddress of the creator of the request 
	 */
	public void setConsumer(RoleAddress consumerAddress) {
		this.consumer = consumerAddress;
	}

	/**
	 * 
	 * @return the quantity of energy needed
	 */
	public double getElectricEnergyRequest() {
		return this.electricEnergyRequest;
	}

	/**
	 * 
	 * @return the RoleAddress of the creator of this request
	 */
	public RoleAddress getConsumer() {
		return this.consumer;
	}
	
	/**
	 * 
	 * @return the date of the request creation
	 */
	public Date getIdentifyDate() {
		return this.date;
	}

	/**
	 * 
	 * @return the Priority
	 */
	public Priority getPriority() {
		return priority;
	}	
	/**************************************/
	/******** End Getter and Setter *******/
	/**************************************/
	
	@Override
	public String toString() {
		return "Request [electricEnergyRequest=" + electricEnergyRequest
				+ ", consumer=" + consumer + ", date=" + date + ", priority="
				+ priority + "]";
	}

	/**
	 * Enum to define the priority of a request
	 * @author Anthony
	 *
	 */
	public enum Priority {
		VERY_LOW,
		LOW,
		MEDIUM,
		HIGH,
		VERY_HIGH		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((consumer == null) ? 0 : consumer.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		long temp;
		temp = Double.doubleToLongBits(electricEnergyRequest);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((priority == null) ? 0 : priority.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Request other = (Request) obj;
		if (consumer == null) {
			if (other.consumer != null)
				return false;
		} else if (!consumer.equals(other.consumer))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		return true;
	}
}
