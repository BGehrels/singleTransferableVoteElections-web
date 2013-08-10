package info.gehrels.voting.web;

import info.gehrels.voting.genderedElections.GenderedElection;

import java.util.ArrayList;
import java.util.List;

public class BallotLayout {
	private List<GenderedElection> elections = new ArrayList<>();

	public void addElection(GenderedElection election) {
		elections.add(election);
	}

	public List<GenderedElection> getElections() {
		return elections;
	}

	public void setElections(List<GenderedElection> elections) {
		this.elections = elections;
	}
}
