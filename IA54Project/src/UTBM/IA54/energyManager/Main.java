package UTBM.IA54.energyManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.logging.Level;

import org.janusproject.kernel.Kernel;
import org.janusproject.kernel.agent.Kernels;
import org.janusproject.kernel.logger.LoggerUtil;

import UTBM.IA54.view.IAView;
import utbm.p13.tx52.connection.Servers;
import utbm.p13.tx52.vehicle.AbstractHybridVehicle;


public class Main {
	public static void main(String[] args) {
		LoggerUtil.setGlobalLevel(Level.ALL);
		LoggerUtil.setShortLogMessageEnable(true);		
		Kernel k = Kernels.get(true);
		
		int nb=4;
		
		List<HashMap<Car.CarComponents, String>> agentNamesList= new ArrayList();
		List<Car> carsList = new ArrayList<>();
		
		for(int i=1;i<=nb;i++){
			HashMap<Car.CarComponents, String> agentNames = new HashMap<Car.CarComponents, String>();
			agentNames.put(Car.CarComponents.BATTERY, "battery "+i);
			agentNames.put(Car.CarComponents.THERMAL_ENGINE, "TE "+i);
			agentNames.put(Car.CarComponents.SREC, "SREC "+i);
			agentNames.put(Car.CarComponents.PROPULSION_ENGINE, "Propulsion engine "+i);
			
			agentNamesList.add(agentNames);
			
			Car car = new Car(i*2, k);
			car.getBattery().setEnergyStored(0.0);
			car.getPropulsion().setEnergyConsume(0.0);
			car.getSrec().setEnergyProvided(0.0);
			car.getTe().setEnergyProvided(0.0);
			
			carsList.add(car);
		}
		
		
		//creation of the servers for the vivus connection
		List<AbstractHybridVehicle> vList = new ArrayList<>();
		for(Car c : carsList)
			vList.add(c.getV());
		Servers servers = new Servers(vList);
        servers.start();
		
        for(int i=0;i<nb;i++){
        	carsList.get(i).launch(agentNamesList.get(i));
        }
        //IA View update by TimerTask
		Timer t = new Timer("IA");
        t.schedule(new IAView(vList),0,1);
		
	}
}
