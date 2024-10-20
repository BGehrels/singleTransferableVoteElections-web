/*
 * Copyright © 2014 Benjamin Gehrels
 *
 * This file is part of The Single Transferable Vote Elections Web Interface.
 *
 * The Single Transferable Vote Elections Web Interface is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Single Transferable Vote Elections Web Interface is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with The Single Transferable Vote
 * Elections Web Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package info.gehrels.voting.web.ballotCasting;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import info.gehrels.voting.web.applicationState.BallotLayout;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

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
            vote.ifPresent(preferenceSetBuilder::add);
			i++;
		}

		return new Ballot<>(ballotId, preferenceSetBuilder.build());
	}

	public void validate(BindingResult bindingResult, String objectName) {
		for (int i=0; i < votesByElectionId.size();i++) {
			votesByElectionId.get(i).validate(objectName, "votesByElectionId[" + i + "]", bindingResult);
		}
	}
}
