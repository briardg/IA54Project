package UTBM.IA54.message;

import org.janusproject.kernel.message.ObjectMessage;

import UTBM.IA54.capacity.Request;

public class EnergyRequestMessage extends ObjectMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7305040445701763174L;

	public EnergyRequestMessage(Request request) {
		super(request);
	}
	
	public Request getRequest() {
		return (Request)this.getContent();
	}
}