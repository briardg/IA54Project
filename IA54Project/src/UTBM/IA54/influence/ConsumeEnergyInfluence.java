package UTBM.IA54.influence;

import UTBM.IA54.capacity.Request;

public class ConsumeEnergyInfluence extends RequestFinalizedInfluence {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3658070015616413756L;

	public ConsumeEnergyInfluence(Object source, Request request) {
		super(source, ConsumeEnergyInfluence.class.getName(), request);
	}
}
