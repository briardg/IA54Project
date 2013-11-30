package UTBM.IA54.message;

import org.janusproject.kernel.message.ObjectMessage;

import UTBM.IA54.capacity.ProposalFinalized;

public class TransferEnergyMessage extends ObjectMessage{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4411551477896373586L;

	public TransferEnergyMessage(ProposalFinalized proposal) {
		super(proposal);
	}
	
	public ProposalFinalized getFinalizeProposal() {
		return (ProposalFinalized)this.getContent();
	}
}
