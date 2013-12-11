package UTBM.IA54.torqueEnergyExchange;

import org.janusproject.kernel.crio.core.CRIOContext;
import org.janusproject.kernel.crio.core.Organization;

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
