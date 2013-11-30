package UTBM.IA54.message;

import org.janusproject.kernel.message.ObjectMessage;

import UTBM.IA54.capacity.Proposal;

public class ProposalEnergyMessage extends ObjectMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7976626016416265459L;

	public ProposalEnergyMessage(Proposal proposal) {
		super(proposal);
	}
	
	public Proposal getProposal() {
		return (Proposal)this.getContent();
	}
}
