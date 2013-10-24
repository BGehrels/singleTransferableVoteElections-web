package info.gehrels.voting.web;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;

import javax.validation.constraints.Min;
import java.util.List;

public final class CastVoteBuilder {
	@Min(0)
	private int ballotId;

	private List<VoteBuilder> votesByElectionId;

	public int getBallotId() {
		return ballotId;
	}

	public void setBallotId(int ballotId) {
		this.ballotId = ballotId;
	}

	public List<VoteBuilder> getVotesByElectionId() {
		return votesByElectionId;
	}

	public void setVotesByElectionId(List<VoteBuilder> votesByElectionId) {
		this.votesByElectionId = votesByElectionId;
	}


	public Ballot<GenderedCandidate> createBallotFromForm(BallotLayout ballotLayout) {
		int i = 0;
		Builder<Vote<GenderedCandidate>> preferenceSetBuilder = ImmutableSet.builder();
		for (GenderedElection genderedElection : ballotLayout.getElections()) {
			Vote<GenderedCandidate> preference =  votesByElectionId.get(i).createPreference(genderedElection);
			preferenceSetBuilder.add(preference);
			i++;
		}

		return new Ballot<>(ballotId, preferenceSetBuilder.build());
	}
}
