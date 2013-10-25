package info.gehrels.voting.web;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public final class BallotBuilder {
	@Min(0) @NotNull
	private Integer ballotId;

	@Valid @NotNull
	private List<VoteBuilder> votesByElectionId;

	public Integer getBallotId() {
		return ballotId;
	}

	public void setBallotId(Integer ballotId) {
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
			Optional<Vote<GenderedCandidate>> vote = votesByElectionId.get(i).createVote(genderedElection);
			if (vote.isPresent()) {
				preferenceSetBuilder.add(vote.get());
			}
			i++;
		}

		return new Ballot<>(ballotId, preferenceSetBuilder.build());
	}
}
