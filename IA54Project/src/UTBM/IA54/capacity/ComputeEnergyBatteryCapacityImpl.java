package UTBM.IA54.capacity;

import org.janusproject.kernel.crio.capacity.CapacityContext;
import org.janusproject.kernel.crio.capacity.CapacityImplementation;
import org.janusproject.kernel.crio.capacity.CapacityImplementationType;

public class ComputeEnergyBatteryCapacityImpl 
extends CapacityImplementation 
implements ComputeElectricEnergyCapacity {
	
	public ComputeEnergyBatteryCapacityImpl() {
		super(CapacityImplementationType.DIRECT_ACTOMIC);
	}
	
	@Override
	public void call(CapacityContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
