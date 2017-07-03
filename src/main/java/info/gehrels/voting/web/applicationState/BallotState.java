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
package info.gehrels.voting.web.applicationState;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

public class BallotState {
	private BallotLayout ballotLayout;
	private Collection<Ballot<GenderedCandidate>> firstTryCastBallots = new ArrayList<>();
	private Collection<Ballot<GenderedCandidate>> secondTryCastBallots = new ArrayList<>();

	public synchronized GenderedElection changeOfficeName(String oldOfficeName, String newOfficeName) {
		GenderedElection genderedElection = ballotLayout.replaceElection(oldOfficeName, (e) -> e.withOfficeName(newOfficeName));
		firstTryCastBallots = firstTryCastBallots.stream().map((Ballot<GenderedCandidate> b) -> withReplacedElection(oldOfficeName, genderedElection, b)).collect(toList());
		secondTryCastBallots = secondTryCastBallots.stream().map((Ballot<GenderedCandidate> b) -> withReplacedElection(oldOfficeName, genderedElection, b)).collect(toList());
		return genderedElection;
	}

	public synchronized BallotLayout getBallotLayout() {
		return ballotLayout;
	}

	public synchronized void setBallotLayout(BallotLayout ballotLayout) {
		this.ballotLayout = ballotLayout;
		firstTryCastBallots.clear();
		secondTryCastBallots.clear();
	}

	public synchronized boolean isBallotLayoutPresent() {
		return ballotLayout != null;
	}



	public synchronized void deleteById(long ballotId) {
		Iterables.removeIf(firstTryCastBallots, hasId(ballotId));
		Iterables.removeIf(secondTryCastBallots, hasId(ballotId));
	}

	public synchronized void addCastBallot(BallotInputTry firstOrSecondTry, Ballot<GenderedCandidate> ballotFromForm) {
		getBallotState(firstOrSecondTry).add(ballotFromForm);
	}

	public synchronized ImmutableCollection<Ballot<GenderedCandidate>> getFirstTryCastBallots() {
		return ImmutableList.copyOf(firstTryCastBallots);
	}

	public synchronized ImmutableCollection<Ballot<GenderedCandidate>> getSecondTryCastBallots() {
		return ImmutableList.copyOf(secondTryCastBallots);
	}

	private synchronized BallotIdPredicate hasId(long ballotId) {
		return new BallotIdPredicate(ballotId);
	}

	private synchronized Collection<Ballot<GenderedCandidate>> getBallotState(BallotInputTry firstOrSecondTry) {
		if (firstOrSecondTry == BallotInputTry.FIRST) {
			return firstTryCastBallots;
		} else if (firstOrSecondTry == BallotInputTry.SECOND) {
			return secondTryCastBallots;
		} else {
			throw new IllegalStateException("Unknown enum value " + firstOrSecondTry);
		}
	}

	private Ballot<GenderedCandidate> withReplacedElection(String oldOfficeName, GenderedElection newElectionVersion, Ballot<GenderedCandidate> b) {
		return b.withReplacedElection(oldOfficeName, newElectionVersion);
	}

	private static final class BallotIdPredicate implements Predicate<Ballot<GenderedCandidate>> {
		private final long ballotId;

		private BallotIdPredicate(long ballotId) {
			this.ballotId = ballotId;
		}

		@Override
		public boolean apply(Ballot<GenderedCandidate> input) {
			return ballotId == input.id;
		}
	}
}