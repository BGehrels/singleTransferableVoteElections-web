package info.gehrels.voting.web;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;

import java.util.ArrayList;
import java.util.List;

public final class GenderedElectionBuilderBean {
	private String officeName;
	private int numberOfFemaleExclusivePositions;
	private int numberOfNonFemaleExclusivePositions;
	private List<GenderedCandidateBuilderBean> candidates = new ArrayList<>();

	public GenderedElectionBuilderBean() {
		candidates.add(new GenderedCandidateBuilderBean());
	}

	public GenderedElectionBuilderBean(int numberOfCandidates) {
		for (int i = 0; i < numberOfCandidates; i++) {
			candidates.add(new GenderedCandidateBuilderBean());
		}
	}

	public String getOfficeName() {
		return officeName;
	}

	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}

	public int getNumberOfFemaleExclusivePositions() {
		return numberOfFemaleExclusivePositions;
	}

	public void setNumberOfFemaleExclusivePositions(int numberOfFemaleExclusivePositions) {
		this.numberOfFemaleExclusivePositions = numberOfFemaleExclusivePositions;
	}

	public int getNumberOfNonFemaleExclusivePositions() {
		return numberOfNonFemaleExclusivePositions;
	}

	public void setNumberOfNonFemaleExclusivePositions(int numberOfNonFemaleExclusivePositions) {
		this.numberOfNonFemaleExclusivePositions = numberOfNonFemaleExclusivePositions;
	}

	public List<GenderedCandidateBuilderBean> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<GenderedCandidateBuilderBean> candidates) {
		this.candidates = candidates;
	}

	public GenderedElection build() {
		Builder<GenderedCandidate> builder = ImmutableSet.builder();
		for (GenderedCandidateBuilderBean candidate : candidates) {
			builder.add(candidate.build());
		}
		return new GenderedElection(officeName, numberOfFemaleExclusivePositions, numberOfNonFemaleExclusivePositions,
		                            builder.build());
	}

	public void addNewCandidate() {
		candidates.add(candidates.size(), new GenderedCandidateBuilderBean());
	}
}
