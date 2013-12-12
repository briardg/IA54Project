package UTBM.IA54.message;

import org.janusproject.kernel.message.ObjectMessage;

import UTBM.IA54.capacity.Proposal;
import UTBM.IA54.capacity.ProposalFinalized;

/**
 * Message contenting a answer to a {@link Proposal}
 * @author Anthony
 *
 */
public class ProposalFinalizedEnergyMessage extends ObjectMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7010167034008854561L;

	public ProposalFinalizedEnergyMessage(ProposalFinalized proposal) {
		super(proposal);
	}
	
	/**
	 * 
	 * @return the Proposal
	 */
	public ProposalFinalized getProposalFinalized() {
		return (ProposalFinalized)this.getContent();
	}
}
