package info.gehrels.voting.web;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.AmbiguityResolver.AmbiguityResolverResult;
import info.gehrels.voting.Candidate;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import org.hibernate.validator.constraints.NotEmpty;

import static com.google.common.collect.Iterables.find;

public class AmbiguityResolverResultBuilderBean {
	@NotEmpty
	private String candidateName;

	@NotEmpty
	private String comment;

	public AmbiguityResolverResult<GenderedCandidate> build(ImmutableSet<GenderedCandidate> candidates) throws
		NoSuchCandidateException {
		// TODO: ensure, that names are Unique!
		GenderedCandidate genderedCandidate = find(candidates, byName(candidateName));
		if (genderedCandidate == null) {
			throw new NoSuchCandidateException(candidateName);
		}
		return new AmbiguityResolverResult<>(genderedCandidate, comment);
	}


	private CandidateWithName byName(String name) {
		return new CandidateWithName(name);
	}

	public void setCandidateName(String candidateName) {
		this.candidateName = candidateName;
	}

	public String getCandidateName() {
		return candidateName;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment;
	}

	private static class CandidateWithName implements Predicate<Candidate> {
		private final String candidateName;

		private CandidateWithName(String candidateName) {
			this.candidateName = candidateName;
		}

		@Override
		public boolean apply(Candidate input) {
			return input.getName().equals(candidateName);
		}
	}

}
