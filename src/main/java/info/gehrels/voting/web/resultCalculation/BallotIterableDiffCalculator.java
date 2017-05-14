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

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Candidate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

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
		Set<Long> setAsDuplicateIds = findDuplicateIds(ballotSetA);
		Set<Long> setBsDuplicateIds = findDuplicateIds(ballotSetB);

		Set<Long> inAButNotInB = inAButNotInB(ballotSetA, ballotSetB);
		inAButNotInB.removeAll(setAsDuplicateIds);

		Set<Long> inBButNotInA = inAButNotInB(ballotSetB, ballotSetA);
		inBButNotInA.removeAll(setBsDuplicateIds);

		Set<Long> differentBetweenTheTwoSets = differentBetweenSetAAndSetB(ballotSetA, ballotSetB);
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
		private final ImmutableSortedSet<Long> setAsDuplicateIds;
		private final ImmutableSortedSet<Long> setBsDuplicateIds;
		private final ImmutableSortedSet<Long> inAButNotInB;
		private final ImmutableSortedSet<Long> inBButNotInA;
		private final ImmutableSortedSet<Long> differentBetweenTheTwoSets;

		private BallotIterableDiff(Set<Long> setAsDuplicateIds,
		                           Set<Long> setBsDuplicateIds,
		                           Set<Long> inAButNotInB,
		                           Set<Long> inBButNotInA,
		                           Set<Long> differentBetweenTheTwoSets) {
			this.setAsDuplicateIds = ImmutableSortedSet.copyOf(setAsDuplicateIds);
			this.setBsDuplicateIds = ImmutableSortedSet.copyOf(setBsDuplicateIds);
			this.inAButNotInB = ImmutableSortedSet.copyOf(inAButNotInB);
			this.inBButNotInA = ImmutableSortedSet.copyOf(inBButNotInA);
			this.differentBetweenTheTwoSets = ImmutableSortedSet.copyOf(differentBetweenTheTwoSets);
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

		public ImmutableSortedSet<Long> getSetAsDuplicateIds() {
			return setAsDuplicateIds;
		}

		public ImmutableSortedSet<Long> getSetBsDuplicateIds() {
			return setBsDuplicateIds;
		}

		public ImmutableSortedSet<Long> getInAButNotInB() {
			return inAButNotInB;
		}

		public ImmutableSortedSet<Long> getInBButNotInA() {
			return inBButNotInA;
		}

		public ImmutableSortedSet<Long> getDifferentBetweenTheTwoSets() {
			return differentBetweenTheTwoSets;
		}

		public boolean isBallotDifferentOrDuplicate(long ballotId) {
			return setAsDuplicateIds.contains(ballotId) ||
			       setBsDuplicateIds.contains(ballotId) ||
			       inAButNotInB.contains(ballotId) ||
			       inBButNotInA.contains(ballotId) ||
			       differentBetweenTheTwoSets.contains(ballotId);
		}
	}
}
