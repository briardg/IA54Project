package UTBM.IA54.capacity;

import org.janusproject.kernel.crio.core.RoleAddress;

/**
 * Proposal class defines an answer to a Request which contents the quantity of energy which could
 * be provided, the RoleAddress of the Provider and the request.
 * @author Anthony
 *
 */
public class Proposal {
	/**
	 * Quantity of energy which could be provided
	 */
	private double electricEnergyProposal;
	/**
	 * the provide
	 */
	private RoleAddress provider;
	/**
	 * the request
	 */
	private Request request;
	
	/**
	 * Construct a Proposal according to a Request
	 * @param energyQuantity quantity of energy proposed
	 * @param req the request 
	 */
	public Proposal(double energyQuantity, Request req) {
		this.provider = null;
		this.electricEnergyProposal = energyQuantity;
		this.request = req;
	}
	
	/**
	 * Construct a Proposal according to a request. This Proposal is provided by the roleProvider
	 * @param roleProvider the address of the role which provides this Proposal
	 * @param energyQuantity quantity of energy proposed
	 * @param req the request
	 */
	public Proposal(RoleAddress roleProvider, double energyQuantity, Request req) {
		this.provider = roleProvider;
		this.electricEnergyProposal = energyQuantity;
		this.request = req;
	}
	
	/*************************************/
	/********* Getter and Setter *********/
	/*************************************/
	public void setProvider(RoleAddress provider) {
		this.provider = provider;
	}

	public double getElectricEnergyProposal() {
		return this.electricEnergyProposal;
	}

	public RoleAddress getProvider() {
		return this.provider;
	}
	
	public Request getRequest() {
		return this.request;
	}
	/*************************************/
	/******* End Getter and Setter *******/
	/*************************************/
	
	@Override
	public String toString() {
		return "Proposal [electricEnergyProposal=" + electricEnergyProposal
				+ ", provider=" + provider + ", request=" + request + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(electricEnergyProposal);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result
				+ ((provider == null) ? 0 : provider.hashCode());
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
		Proposal other = (Proposal) obj;
		if (Double.doubleToLongBits(electricEnergyProposal) != Double
				.doubleToLongBits(other.electricEnergyProposal))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		return true;
	}	
}
