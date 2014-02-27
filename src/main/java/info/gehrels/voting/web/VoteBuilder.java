package info.gehrels.voting.web;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Iterables;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;

import javax.validation.constraints.NotNull;

public final class VoteBuilder {
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
			int candidateIndex = c-'A';
			preferenceBuilder.add(Iterables.get(genderedElection.getCandidates(), candidateIndex));
		}
		return Vote.createPreferenceVote(genderedElection, preferenceBuilder.build());
	}

	private enum Type {
		PREFERENCE, NO, NOT_VOTED, INVALID
	}
}
