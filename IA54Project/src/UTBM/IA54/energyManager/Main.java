package UTBM.IA54.energyManager;

import java.util.HashMap;
import java.util.logging.Level;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

public class Main {
	public static void main(String[] args) {
		LoggerUtil.setGlobalLevel(Level.ALL);
		LoggerUtil.setShortLogMessageEnable(true);		
		Kernel k = Kernels.get(true);
		
		// Car 1
		HashMap<Car.CarComponents, String> agentNames1 = new HashMap<Car.CarComponents, String>();
		agentNames1.put(Car.CarComponents.BATTERY, "battery 1");
		agentNames1.put(Car.CarComponents.THERMAL_ENGINE, "TE 1");
		agentNames1.put(Car.CarComponents.SREC, "SREC 1");
		agentNames1.put(Car.CarComponents.PROPULSION_ENGINE, "Propulsion engine 1");

		Car car1 = new Car(5.0, k);
			
		car1.getBattery().setEnergyStored(90.0);
		car1.getPropulsion().setEnergyConsume(0.0);
		car1.getSrec().setEnergyProvided(10.0);
		car1.getTe().setEnergyProvided(0.0);
	
		// Car 2
		HashMap<Car.CarComponents, String> agentNames2 = new HashMap<Car.CarComponents, String>();
		agentNames2.put(Car.CarComponents.BATTERY, "battery 2");
		agentNames2.put(Car.CarComponents.THERMAL_ENGINE, "TE 2");
		agentNames2.put(Car.CarComponents.SREC, "SREC 2");
		agentNames2.put(Car.CarComponents.PROPULSION_ENGINE, "Propulsion engine 2");

		Car car2 = new Car(7.0, k);
		
		car2.getBattery().setEnergyStored(90.0);
		car2.getPropulsion().setEnergyConsume(0.0);
		car2.getSrec().setEnergyProvided(10.0);
		car2.getTe().setEnergyProvided(0.0);
		
		car1.launch(agentNames1);
		car2.launch(agentNames2);
	}
}
