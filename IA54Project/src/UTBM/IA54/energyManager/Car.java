package UTBM.IA54.energyManager;
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
		this.battery.setEnergyStored(90);
		this.te = new ThermalEngineAgent(this);
		this.srec = new SRECAgent(this);
		this.propulsion = new PropulsionEngineAgent(this);
		this.propulsion.setEnergyConsume(0);
		
		k.launchLightAgent(this.battery, "battery");
		k.launchLightAgent(this.te, "THERMAL_ENGINE");
		k.launchLightAgent(this.srec, "SREV");
		k.launchLightAgent(this.propulsion, "PROPULSION_ENGINE");
	}

	public double getPosition() {
		return this.position;
	}		
}
