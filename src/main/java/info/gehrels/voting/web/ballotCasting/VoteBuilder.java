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
package info.gehrels.voting.web.ballotCasting;

import com.google.common.collect.ImmutableList;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public final class VoteBuilder {
	@NotNull
	private VoteType type = VoteType.PREFERENCE;

	@Valid
	@NotNull
	private List<PreferenceBuilder> preferencesByCandidateIdx = new ArrayList<>();

	public VoteType getType() {
		return type;
	}

	public void setType(VoteType type) {
		this.type = type;
	}

	public List<PreferenceBuilder> getPreferencesByCandidateIdx() {
		return preferencesByCandidateIdx;
	}

	public void setPreferencesByCandidateIdx(List<PreferenceBuilder> preferencesByCandidateIdx) {
		this.preferencesByCandidateIdx = preferencesByCandidateIdx;
	}

	public Optional<Vote<GenderedCandidate>> createVote(GenderedElection genderedElection) {
		switch (type) {
			case PREFERENCE:
				return Optional.of(createPreferenceVote(genderedElection));
			case NO:
				return Optional.of(Vote.createNoVote(genderedElection));
			case INVALID:
				return Optional.of(Vote.createInvalidVote(genderedElection));
			case NOT_VOTED:
				return Optional.empty();
			default:
				throw new IllegalStateException("Unknown type " + type);
		}
	}

	private Vote<GenderedCandidate> createPreferenceVote(GenderedElection genderedElection) {
		SortedMap<Long, GenderedCandidate> candidatesByPreferences = new TreeMap<>();

		for (int i = 0; (i < genderedElection.getCandidates().size()) && (i < preferencesByCandidateIdx.size()); i++) {
			PreferenceBuilder preference = preferencesByCandidateIdx.get(i);
			if (preference.getValue() != null) {
				candidatesByPreferences
					.put(preference.getValue(), genderedElection.getCandidates().asList().get(i));
			}
		}

		return Vote.createPreferenceVote(genderedElection, ImmutableList.copyOf(candidatesByPreferences.values()));
	}

	public void validate(String objectName, String fieldPrefix, BindingResult bindingResult) {
		if (type == VoteType.PREFERENCE) {
			if (allNullOrEmpty(preferencesByCandidateIdx)) {
				bindingResult.addError(
					new FieldError(objectName, fieldPrefix + ".preferencesByCandidateIdx", "", true, new String[]{"emptyPreference"}, new Object[]{},
					               "You must enter a preference if you have selected it."));
			} else if (containsDuplicates(preferencesByCandidateIdx)) {
				bindingResult.addError(
					new FieldError(objectName, fieldPrefix + ".preferencesByCandidateIdx", "", true, new String[]{"duplicatesInPreference"}, new Object[]{},
					               "You must enter a duplicate free preference."));
			}
		} else {
			if (!allNullOrEmpty(preferencesByCandidateIdx)) {
				bindingResult.addError(
					new FieldError(objectName, fieldPrefix + ".preferencesByCandidateIdx", "", true, new String[]{"noPreferenceAllowed"}, new Object[]{},
					               "You must not enter a preference for no, invalid or non casted votes."));
			}
		}
	}

	private boolean allNullOrEmpty(List<PreferenceBuilder> preferenceBuilders) {
		for (PreferenceBuilder preferenceBuilder : preferenceBuilders) {
			if (isNonEmptyPreference(preferenceBuilder)) {
				return false;
			}
		}

		return true;
	}

	private boolean isNonEmptyPreference(PreferenceBuilder preferenceBuilder) {
		return preferenceBuilder != null && preferenceBuilder.getValue() != null;
	}


	private boolean containsDuplicates(List<PreferenceBuilder> list) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = i + 1; j < list.size(); j++) {
				if (isNonEmptyPreference(list.get(i)) && list.get(i).equals(list.get(j))) {
					return true;
				}
			}
		}

		return false;
	}

}
