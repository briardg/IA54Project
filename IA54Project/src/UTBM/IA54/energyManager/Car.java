package UTBM.IA54.energyManager;
import org.arakhne.afc.vmutil.locale.Locale;
import org.janusproject.kernel.Kernel;

import UTBM.IA54.agents.BatteryAgent;
import UTBM.IA54.agents.PropulsionEngineAgent;
import UTBM.IA54.agents.SRECAgent;
import UTBM.IA54.agents.ThermalEngineAgent;


public class Car {
	private double position;
	
	private BatteryAgent battery;
	private ThermalEngineAgent te;
	private SRECAgent srec;
	private PropulsionEngineAgent propulsion;
	
	
	public Car(double pos, Kernel k) {
		this.position = pos;
		
		this.battery = new BatteryAgent(this);
		this.te = new ThermalEngineAgent(this);
		this.srec = new SRECAgent(this);
		this.propulsion = new PropulsionEngineAgent(this);
		
		k.launchHeavyAgent(this.battery, Locale.getString(Car.class, "BATTERY", 1));
		k.launchHeavyAgent(this.te, Locale.getString(Car.class, "THERMAL_ENGINE", 2));
		k.launchHeavyAgent(this.srec, Locale.getString(Car.class, "SREV", 3));
		k.launchHeavyAgent(this.propulsion, Locale.getString(Car.class, "PROPULSION_ENGINE", 4));
	}

	public double getPosition() {
		return this.position;
	}		
}
