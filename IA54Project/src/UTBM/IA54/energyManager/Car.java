package UTBM.IA54.energyManager;
import java.util.HashMap;

import org.janusproject.kernel.Kernel;

import utbm.p13.tx52.battery.LithiumBattery;
import utbm.p13.tx52.motor.ElectricMotor;
import utbm.p13.tx52.motor.Engine;
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
	private Kernel kernel;
	
	
	public Car(double pos, Kernel k) {
		this.position = pos;
		this.kernel = k;
		
		this.battery = new BatteryAgent(this,new LithiumBattery(12, 1000));
		this.te = new ThermalEngineAgent(this,new Engine(60,0.75,0.195,200));
		this.propulsion = new PropulsionEngineAgent(this,new ElectricMotor(0.5, 2, 0.5, 1000, 0.04, 14,0));
		this.srec = new SRECAgent(this);
	}

	public double getPosition() {
		return this.position;
	}		
	
	public void launch(HashMap<CarComponents, String> map) {
		if(map.containsKey(CarComponents.BATTERY)) {
			this.kernel.launchLightAgent(this.battery, map.get(CarComponents.BATTERY));
		}
		
		if(map.containsKey(CarComponents.PROPULSION_ENGINE)) {
			this.kernel.launchLightAgent(this.propulsion, map.get(CarComponents.PROPULSION_ENGINE));
		}
		
		if(map.containsKey(CarComponents.SREC)) {
			this.kernel.launchLightAgent(this.srec, map.get(CarComponents.SREC));
		}
		
		if(map.containsKey(CarComponents.THERMAL_ENGINE)) {
			this.kernel.launchLightAgent(this.te, map.get(CarComponents.THERMAL_ENGINE));
		}
	}

	public BatteryAgent getBattery() {
		return battery;
	}

	public void setBattery(BatteryAgent battery) {
		this.battery = battery;
	}

	public ThermalEngineAgent getTe() {
		return te;
	}

	public void setTe(ThermalEngineAgent te) {
		this.te = te;
	}

	public SRECAgent getSrec() {
		return srec;
	}

	public void setSrec(SRECAgent srec) {
		this.srec = srec;
	}

	public PropulsionEngineAgent getPropulsion() {
		return propulsion;
	}

	public void setPropulsion(PropulsionEngineAgent propulsion) {
		this.propulsion = propulsion;
	}
	
	public enum CarComponents {
		BATTERY,
		THERMAL_ENGINE,
		PROPULSION_ENGINE,
		SREC
	}
}
