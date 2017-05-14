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

import java.util.ArrayList;
import java.util.Collection;

public final class CastBallotsState {
	private final Collection<Ballot<GenderedCandidate>> firstTryCastBallots = new ArrayList<>();
	private final Collection<Ballot<GenderedCandidate>> secondTryCastBallots = new ArrayList<>();

	public synchronized void deleteById(long ballotId) {
		Iterables.removeIf(firstTryCastBallots, hasId(ballotId));
		Iterables.removeIf(secondTryCastBallots, hasId(ballotId));
	}

	public synchronized void add(BallotInputTry firstOrSecondTry, Ballot<GenderedCandidate> ballotFromForm) {
		getCastBallotsState(firstOrSecondTry).add(ballotFromForm);
	}

	public synchronized ImmutableCollection<Ballot<GenderedCandidate>> getFirstTryCastBallots() {
		return ImmutableList.copyOf(firstTryCastBallots);
	}

	public synchronized ImmutableCollection<Ballot<GenderedCandidate>> getSecondTryCastBallots() {
		return ImmutableList.copyOf(secondTryCastBallots);
	}

	private BallotIdPredicate hasId(long ballotId) {
		return new BallotIdPredicate(ballotId);
	}

	private Collection<Ballot<GenderedCandidate>> getCastBallotsState(BallotInputTry firstOrSecondTry) {
		if (firstOrSecondTry == BallotInputTry.FIRST) {
			return firstTryCastBallots;
		} else if (firstOrSecondTry == BallotInputTry.SECOND) {
			return secondTryCastBallots;
		} else {
			throw new IllegalStateException("Unknown enum value " + firstOrSecondTry);
		}
	}

	public synchronized void reset() {
		firstTryCastBallots.clear();
		secondTryCastBallots.clear();
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
