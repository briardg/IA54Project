package UTBM.IA54.influence;

import UTBM.IA54.capacity.Request;

public class ProvideEnergyInfluence extends RequestFinalizedInfluence {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3610856784318466808L;


	public ProvideEnergyInfluence(Object source, Request request) {
		super(source, ProvideEnergyInfluence.class.getName(), request);
	}
}
