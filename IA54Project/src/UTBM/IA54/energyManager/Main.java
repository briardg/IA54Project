package UTBM.IA54.energyManager;

import java.util.logging.Level;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.crio.core.GroupAddress;
import org.janusproject.kernel.logger.LoggerUtil;


public class Main {
public static GroupAddress g;
	public static void main(String[] args) {
		LoggerUtil.setGlobalLevel(Level.ALL);
		LoggerUtil.setShortLogMessageEnable(true);		
		Kernel k = Kernels.get(true);
		
		Car car = new Car(5.0, k);
	}

}
