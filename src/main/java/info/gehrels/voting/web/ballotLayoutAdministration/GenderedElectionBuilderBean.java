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
package info.gehrels.voting.web.ballotLayoutAdministration;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import  jakarta.validation.constraints.NotEmpty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

public final class GenderedElectionBuilderBean {
	@NotEmpty
	private String officeName;
	@Min(0)
	private int numberOfFemaleExclusivePositions;
	@Min(0)
	private int numberOfNotFemaleExclusivePositions;
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

	public int getNumberOfNotFemaleExclusivePositions() {
		return numberOfNotFemaleExclusivePositions;
	}

	public void setNumberOfNotFemaleExclusivePositions(int numberOfNotFemaleExclusivePositions) {
		this.numberOfNotFemaleExclusivePositions = numberOfNotFemaleExclusivePositions;
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
		return new GenderedElection(officeName, numberOfFemaleExclusivePositions, numberOfNotFemaleExclusivePositions,
		                            builder.build());
	}

	public void addNewCandidate() {
		candidates.add(candidates.size(), new GenderedCandidateBuilderBean());
	}

	public void deleteCandidate(int i) {
		// keep at least one candidate
		if (candidates.size() > 1) {
			candidates.remove(i);
		}
	}
}
