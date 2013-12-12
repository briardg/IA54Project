package UTBM.IA54.torqueEnergyExchange;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

/**
 * Torque organization. Defines how providers produce torque, and defines how consumers
 * consume torque
 * @author Anthony
 *
 */
public class TorqueOrganization extends Organization {

	/**
	 * 
	 * @param crioContext
	 */
	protected TorqueOrganization(CRIOContext crioContext) {
		super(crioContext);
		addRole(TorqueProvider.class);
	}
}
