package UTBM.IA54.capacity;

import org.janusproject.kernel.crio.core.RoleAddress;

public class Proposal {
	
	private double electricEnergyProposal;
	private RoleAddress provider;
	
	public Proposal(RoleAddress roleProvider, double energyProposal) {
		this.provider = roleProvider;
		this.electricEnergyProposal = energyProposal;
	}

	public double getElectricEnergyProposal() {
		return this.electricEnergyProposal;
	}

	public RoleAddress getProvider() {
		return this.provider;
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
