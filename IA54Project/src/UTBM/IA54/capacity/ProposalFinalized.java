package UTBM.IA54.capacity;

/**
 * ProposalFinalized object is an answer to a proposal given by the consumer to a provider
 * @author Anthony
 *
 */
public class ProposalFinalized {
	/**
	 * a proposal
	 */
	private Proposal proposal;
	
	/**
	 * 
	 * @param proposalFinalized a proposal
	 */
	public ProposalFinalized(Proposal proposalFinalized) {
		this.proposal = proposalFinalized;
	}

	/**
	 * Return null if the consumer role doesn't want to make a transaction with the provider role
	 * @return the proposal or null
	 */
	public Proposal getProposal() {
		return this.proposal;
	}

	@Override
	public String toString() {
		return "ProposalFinalized [proposal=" + proposal + "]";
	}
}