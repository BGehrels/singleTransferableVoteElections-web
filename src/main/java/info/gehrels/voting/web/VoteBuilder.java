package info.gehrels.voting.web;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;

public final class VoteBuilder {
	private Type type;
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

	public Vote<GenderedCandidate> createPreference(GenderedElection genderedElection) {
		Builder<GenderedCandidate> preferenceBuilder = ImmutableSet.builder();
		for (char c : preferenceString.toUpperCase().toCharArray()) {
			int candidateIndex = c-'A';
			preferenceBuilder.add(Iterables.get(genderedElection.getCandidates(), candidateIndex));
		}
		return Vote.createPreferenceVote(genderedElection, preferenceBuilder.build());
	}

	private enum Type {
		PREFERENCE, NO, NOT_VOTED
	}
}
