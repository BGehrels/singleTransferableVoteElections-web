package info.gehrels.voting.web;

import info.gehrels.voting.genderedElections.GenderedCandidate;

public class GenderedCandidateBuilderBean {
	private String name;
	private boolean female;

	public GenderedCandidateBuilderBean() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFemale() {
		return female;
	}

	public void setFemale(boolean female) {
		this.female = female;
	}

	public GenderedCandidate build() {
		return new GenderedCandidate(name, female);
	}
}
