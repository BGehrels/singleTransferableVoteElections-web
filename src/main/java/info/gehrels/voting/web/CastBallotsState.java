package info.gehrels.voting.web;

import info.gehrels.voting.Ballot;
import info.gehrels.voting.genderedElections.GenderedCandidate;

import java.util.HashMap;
import java.util.Map;

public class CastBallotsState {
	public final Map<Integer, Ballot<GenderedCandidate>> castBallotsById = new HashMap<>();
}
