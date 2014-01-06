package UTBM.IA54.electricEnergyExchange;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * 
 * @author Anthony et Gautier
 *
 */
public class ElectricEnergyExchangeOrganization extends Organization {

	/**
	 * 
	 * @param crioContext
	 */
	public ElectricEnergyExchangeOrganization(CRIOContext crioContext) {
		super(crioContext);		
		addRole(ElectricEnergyProvider.class);
		addRole(ElectricEnergyConsumer.class);
	}
}


