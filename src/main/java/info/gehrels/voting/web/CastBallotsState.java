package info.gehrels.voting.web;

import com.google.common.base.Predicate;
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

	public synchronized Collection<Ballot<GenderedCandidate>> getFirstTryCastBallots() {
		return ImmutableList.copyOf(firstTryCastBallots);
	}

	public synchronized Collection<Ballot<GenderedCandidate>> getSecondTryCastBallots() {
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
