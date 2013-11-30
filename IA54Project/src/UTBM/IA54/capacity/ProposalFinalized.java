package UTBM.IA54.capacity;

public class ProposalFinalized {
	
	private Proposal proposal;
	
	public ProposalFinalized(Proposal proposalFinalized) {
		this.proposal = proposalFinalized;
	}

	/**
	 * Return null if the consumer role doesn't want to make a transaction with the provider role
	 * @return the proposal or null
	 */
	public Proposal getProposal() {
		return proposal;
	}
}