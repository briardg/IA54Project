package UTBM.IA54.capacity;

import java.util.ArrayList;

import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;

/**
 * Implementation of {@link FindBestRequestCapacity}. Defines how find the best request from a list
 * of requests
 * @author Anthony
 *
 */
public class FindBestRequestCapacityImpl
extends CapacityImplementation
implements FindBestRequestCapacity {
	
	public FindBestRequestCapacityImpl() {
		super(CapacityImplementationType.DIRECT_ACTOMIC);
	}

	@Override
	public void call(CapacityContext call) throws Exception {
		// TODO behavior
		
		ArrayList<Request> requests = (ArrayList<Request>)call.getInputValues()[0];		
		call.setOutputValues(requests.get(0));
	}

}
