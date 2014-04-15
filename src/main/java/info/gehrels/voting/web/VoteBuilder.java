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
package info.gehrels.voting.web;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.constraints.NotNull;

public final class VoteBuilder {
	public static final Object[] EMPTY = new Object[0];
	@NotNull
	private Type type = Type.PREFERENCE;

	@NotNull
	private String preferenceString;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getPreferenceString() {
		return preferenceString;
	}

	public void setPreferenceString(String preferenceString) {
		this.preferenceString = preferenceString;
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
				return Optional.absent();
			default:
				throw new IllegalStateException("Unkown type " + type);
		}
	}

	private Vote<GenderedCandidate> createPreferenceVote(GenderedElection genderedElection) {
		Builder<GenderedCandidate> preferenceBuilder = ImmutableSet.builder();
		for (char c : preferenceString.toUpperCase().toCharArray()) {
			int candidateIndex = c - 'A';
			preferenceBuilder.add(Iterables.get(genderedElection.getCandidates(), candidateIndex));
		}
		return Vote.createPreferenceVote(genderedElection, preferenceBuilder.build());
	}

	public void validate(String objectName, String fieldPrefix, BindingResult bindingResult) {
		if ((type == Type.PREFERENCE) && preferenceString.isEmpty()) {
			bindingResult.addError(
				new FieldError(objectName, fieldPrefix + ".preferenceString", preferenceString, false,
				               new String[]{"emptyPreference"}, EMPTY,
				               "You must enter a preference if you selected it."));
		}

		if (containsDuplicateChars(preferenceString)) {
			bindingResult.addError(
				new FieldError(objectName, fieldPrefix + ".preferenceString", preferenceString, false,
				               new String[]{"duplicateCandidate"}, EMPTY,
				               "The Preference must not contain duplicate entries"));
		}
	}

	private static boolean containsDuplicateChars(String preferenceString) {
		for (int i = 0; i < preferenceString.length(); i++) {
			for (int j = i + 1; j < preferenceString.length(); j++) {
				if (preferenceString.charAt(i) == preferenceString.charAt(j)) {
					return true;
				}
			}
		}

		return false;
	}

	private enum Type {
		PREFERENCE, NO, NOT_VOTED, INVALID
	}
}
