package info.gehrels.voting.web;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

public final class GenderedElectionBuilderBean {
	@NotEmpty
	private String officeName;
	@Min(0)
	private int numberOfFemaleExclusivePositions;
	@Min(0)
	private int numberOfNonFemaleExclusivePositions;
	@NotEmpty @Valid
	private List<GenderedCandidateBuilderBean> candidates = new ArrayList<>();

	public GenderedElectionBuilderBean() {
		candidates.add(new GenderedCandidateBuilderBean());
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
