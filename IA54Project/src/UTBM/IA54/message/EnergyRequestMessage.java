package UTBM.IA54.message;

import org.janusproject.kernel.message.ObjectMessage;

import UTBM.IA54.capacity.Request;

/**
 * Message contenting a {@link Request}
 * @author Anthony
 *
 */
public class EnergyRequestMessage extends ObjectMessage {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7305040445701763174L;

	/**
	 * 
	 * @param request the request
	 */
	public EnergyRequestMessage(Request request) {
		super(request);
	}
	
	/**
	 * 
	 * @return the request
	 */
	public Request getRequest() {
		return (Request)this.getContent();
	}
}