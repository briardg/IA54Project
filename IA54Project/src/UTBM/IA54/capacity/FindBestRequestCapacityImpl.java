package UTBM.IA54.capacity;

import java.util.ArrayList;

import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;

public class FindBestRequestCapacityImpl
extends CapacityImplementation
implements FindBestRequestCapacity {
	
	public FindBestRequestCapacityImpl() {
		super(CapacityImplementationType.DIRECT_ACTOMIC);
	}

	@Override
	public void call(CapacityContext call) throws Exception {
		ArrayList<Request> requests = (ArrayList<Request>)call.getInputValues()[0];
		
		// TODO behavior
		call.setOutputValues(requests.get(0));
		
		System.out.println("\nFindBestRequestCapacityImpl, best request :"+requests.get(0));
	}

}
