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
package info.gehrels.voting.web.resultCalculation;

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
