package UTBM.IA54.view;

import java.util.List;
import java.util.TimerTask;

import utbm.p13.tx52.vehicle.AbstractHybridVehicle;
import utbm.p13.tx52.view.MainFrame;

public class IAView extends TimerTask {

	private MainFrame frame ;
	
	public IAView (List<AbstractHybridVehicle> vList){
		this.frame= new MainFrame("IAView", vList);
        this.frame.setVisible(true);
	}
	
	@Override
	public void run() {
		frame.update();
	}

}
