package UTBM.IA54.exchangeEnergy;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * 
 * @author Anthony
 *
 */
public class ExchangeEnergyOrganization extends Organization {

	/**
	 * 
	 * @param crioContext
	 */
	protected ExchangeEnergyOrganization(CRIOContext crioContext) {
		super(crioContext);
		addRole(SendEnergyRole.class);
		addRole(ReceiveEnergyRole.class);
		addRole(ProduceTorqueRole.class);
	}

}
