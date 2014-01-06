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
		ArrayList<Request> requests = (ArrayList<Request>)call.getInputValues()[0];	
		Request bestr = null;
		for(Request r : requests){
			if(bestr == null){
				bestr = r;
			}else if(r.getPriority().ordinal() > bestr.getPriority().ordinal()){
					bestr = r;
			}
		}
		
		call.setOutputValues(bestr);
	}

}
