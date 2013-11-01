package info.gehrels.voting.web;

import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;

import java.util.ArrayList;
import java.util.Collection;

public final class CastBallotsState {
	private final Collection<Ballot<GenderedCandidate>> firstTryCastBallots = new ArrayList<>();
	private final Collection<Ballot<GenderedCandidate>> secondTryCastBallots = new ArrayList<>();

	public void deleteById(long ballotId) {
		throw new UnsupportedOperationException();
	}

	public void add(BallotInputTry firstOrSecondTry, Ballot<GenderedCandidate> ballotFromForm) {
		getCastBallotsState(firstOrSecondTry).add(ballotFromForm);
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

	public Collection<Ballot<GenderedCandidate>> getFirstTryCastBallots() {
		return firstTryCastBallots;
	}

	public Collection<Ballot<GenderedCandidate>> getSecondTryCastBallots() {
		return secondTryCastBallots;
	}
}
