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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Candidate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.collect.Sets.newHashSet;

public final class BallotIterableDiffCalculator {
	private static final Function<Ballot<?>, Long> GET_ID_FROM_BALLOT_FUNCTION = new Function<Ballot<?>, Long>() {
		@Override
		public Long apply(Ballot<?> input) {
			return input.id;
		}
	};

	private BallotIterableDiffCalculator() {
	}

	public static <T extends Candidate> BallotIterableDiff calculateDiff(Iterable<Ballot<T>> ballotSetA,
	                                                                Iterable<Ballot<T>> ballotSetB) {
		Set<Long> setAsDuplicateIds = new TreeSet<Long>(findDuplicateIds(ballotSetA));
		Set<Long> setBsDuplicateIds = new TreeSet<Long>(findDuplicateIds(ballotSetB));

		Set<Long> inAButNotInB = new TreeSet<Long>(inAButNotInB(ballotSetA, ballotSetB));
		inAButNotInB.removeAll(setAsDuplicateIds);

		Set<Long> inBButNotInA = new TreeSet<Long>(inAButNotInB(ballotSetB, ballotSetA));
		inBButNotInA.removeAll(setBsDuplicateIds);

		Set<Long> differentBetweenTheTwoSets = new TreeSet<Long>(differentBetweenSetAAndSetB(ballotSetA, ballotSetB));
		differentBetweenTheTwoSets.removeAll(setAsDuplicateIds);
		differentBetweenTheTwoSets.removeAll(setBsDuplicateIds);
		differentBetweenTheTwoSets.removeAll(inAButNotInB);
		differentBetweenTheTwoSets.removeAll(inBButNotInA);

		return new BallotIterableDiff(setAsDuplicateIds, setBsDuplicateIds, inAButNotInB, inBButNotInA,
		                         differentBetweenTheTwoSets);
	}


	private static <T extends Candidate> Set<Long> differentBetweenSetAAndSetB(Iterable<Ballot<T>> ballotSetA,
	                                                                           Iterable<Ballot<T>> ballotSetB) {
		Set<Long> idsOfDifferingBallots = new HashSet<>();

		Multimap<Long, Ballot<T>> idsToBallotSetMultimapForB = asIdToBallotsMulitmap(ballotSetB);
		for (Ballot<T> ballotFromSetA : ballotSetA) {
			Collection<Ballot<T>> ballotsFromSetB = idsToBallotSetMultimapForB.get(ballotFromSetA.id);
			for (Ballot<T> ballotFromSetB : ballotsFromSetB) {
				if (!ballotFromSetA.equals(ballotFromSetB)) {
					idsOfDifferingBallots.add(ballotFromSetA.id);
				}
			}
		}

		return idsOfDifferingBallots;
	}

	private static <T extends Candidate> Set<Long> inAButNotInB(Iterable<Ballot<T>> ballotSetA,
	                                                            Iterable<Ballot<T>> ballotSetB) {
		Set<Long> idsFromSetA = toMutableIdSet(ballotSetA);
		Set<Long> idsFromSetB = toImmutableIdSet(ballotSetB);
		idsFromSetA.removeAll(idsFromSetB);
		return idsFromSetA;
	}

	private static <T extends Candidate> ImmutableSet<Long> toImmutableIdSet(Iterable<Ballot<T>> ballotSetB) {
		return ImmutableSet.copyOf(Iterables.transform(ballotSetB, GET_ID_FROM_BALLOT_FUNCTION));
	}

	private static <T extends Candidate> Set<Long> toMutableIdSet(Iterable<Ballot<T>> ballotSetA) {
		return newHashSet(Iterables.transform(ballotSetA, GET_ID_FROM_BALLOT_FUNCTION));
	}

	private static <T extends Candidate> Set<Long> findDuplicateIds(Iterable<Ballot<T>> castBallots) {
		Set<Long> duplicateIds = new HashSet<>();

		Multimap<Long, Ballot<T>> castBallotsById = asIdToBallotsMulitmap(castBallots);
		for (Entry<Long, Collection<Ballot<T>>> longCollectionEntry : castBallotsById.asMap()
			.entrySet()) {
			if (longCollectionEntry.getValue().size() > 1) {
				duplicateIds.add(longCollectionEntry.getKey());
			}
		}

		return duplicateIds;
	}

	private static <T extends Candidate> Multimap<Long, Ballot<T>> asIdToBallotsMulitmap(
		Iterable<Ballot<T>> castBallots) {
		return Multimaps
			.index(castBallots,
			       GET_ID_FROM_BALLOT_FUNCTION);
	}

	public static final class BallotIterableDiff {
		private final ImmutableSet<Long> setAsDuplicateIds;
		private final ImmutableSet<Long> setBsDuplicateIds;
		private final ImmutableSet<Long> inAButNotInB;
		private final ImmutableSet<Long> inBButNotInA;
		private final ImmutableSet<Long> differentBetweenTheTwoSets;

		private BallotIterableDiff(Set<Long> setAsDuplicateIds,
		                           Set<Long> setBsDuplicateIds,
		                           Set<Long> inAButNotInB,
		                           Set<Long> inBButNotInA,
		                           Set<Long> differentBetweenTheTwoSets) {
			this.setAsDuplicateIds = ImmutableSet.copyOf(setAsDuplicateIds);
			this.setBsDuplicateIds = ImmutableSet.copyOf(setBsDuplicateIds);
			this.inAButNotInB = ImmutableSet.copyOf(inAButNotInB);
			this.inBButNotInA = ImmutableSet.copyOf(inBButNotInA);
			this.differentBetweenTheTwoSets = ImmutableSet.copyOf(differentBetweenTheTwoSets);
		}

		public boolean isDifferent() {
			return !isEqual();
		}

		public boolean isEqual() {
			return setAsDuplicateIds.isEmpty() &&
			       setBsDuplicateIds.isEmpty() &&
			       inAButNotInB.isEmpty() &&
			       inBButNotInA.isEmpty() &&
			       differentBetweenTheTwoSets.isEmpty();
		}

		public ImmutableSet<Long> getSetAsDuplicateIds() {
			return setAsDuplicateIds;
		}

		public ImmutableSet<Long> getSetBsDuplicateIds() {
			return setBsDuplicateIds;
		}

		public ImmutableSet<Long> getInAButNotInB() {
			return inAButNotInB;
		}

		public ImmutableSet<Long> getInBButNotInA() {
			return inBButNotInA;
		}

		public ImmutableSet<Long> getDifferentBetweenTheTwoSets() {
			return differentBetweenTheTwoSets;
		}
	}
}
