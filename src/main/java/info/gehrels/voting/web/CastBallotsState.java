package info.gehrels.voting.web;

import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;

import java.util.ArrayList;
import java.util.Collection;

public class CastBallotsState {
	public final Collection<Ballot<GenderedCandidate>> firstTryCastBallots = new ArrayList<>();
	public final Collection<Ballot<GenderedCandidate>> secondTryCastBallots = new ArrayList<>();
}
