package info.gehrels.voting.web;

import java.util.List;

public class CastVoteForm {
	private int ballotId;
	private List<String> votesByElectionId;

	public int getBallotId() {
		return ballotId;
	}

	public void setBallotId(int ballotId) {
		this.ballotId = ballotId;
	}

	public List<String> getVotesByElectionId() {
		return votesByElectionId;
	}

	public void setVotesByElectionId(List<String> votesByElectionId) {
		this.votesByElectionId = votesByElectionId;
	}
}
