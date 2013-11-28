package UTBM.IA54.electricEnergyExchange;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * 
 * @author Anthony
 *
 */
public class ElectricEnergyExchangeOrganization extends Organization {

	/**
	 * 
	 * @param crioContext
	 */
	protected ElectricEnergyExchangeOrganization(CRIOContext crioContext) {
		super(crioContext);
		addRole(ElectricEnergyProvider.class);
		addRole(ElectricEnergyConsumer.class);
	}
}
