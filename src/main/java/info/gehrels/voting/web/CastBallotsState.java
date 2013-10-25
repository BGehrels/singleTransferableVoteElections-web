package info.gehrels.voting.web;

import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;

import java.util.HashSet;
import java.util.Set;

public class CastBallotsState {
	public final Set<Ballot<GenderedCandidate>> castBallotsById = new HashSet<>();
}
