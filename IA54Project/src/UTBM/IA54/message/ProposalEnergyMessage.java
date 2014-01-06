package UTBM.IA54.message;

import org.janusproject.kernel.message.ObjectMessage;

import UTBM.IA54.capacity.Proposal;

/**
 * Message contenting a {@link Proposal}
 * @author Anthony et Gautier
 *
 */
public class ProposalEnergyMessage extends ObjectMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7976626016416265459L;

	public ProposalEnergyMessage(Proposal proposal) {
		super(proposal);
	}
	
	/**
	 * 
	 * @return the proposal
	 */
	public Proposal getProposal() {
		return (Proposal)this.getContent();
	}
}
