package UTBM.IA54.influence;

import org.janusproject.kernel.agentsignal.Signal;

import UTBM.IA54.capacity.Request;

public class RequestFinalizedInfluence extends Signal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3320417896504962858L;
	
	public RequestFinalizedInfluence(Object source, String className, Request request) {
		super(source, className, request);
	}

	public Request getRequest() {
		return (Request)this.getValueAt(0);
	}
}
