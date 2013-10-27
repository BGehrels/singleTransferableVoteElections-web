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

import static com.google.common.collect.Sets.newHashSet;

public final class BallotIterableDiffer {
	private static final Function<Ballot<?>, Long> GET_ID_FROM_BALLOT_FUNCTION = new Function<Ballot<?>, Long>() {
		@Override
		public Long apply(Ballot<?> input) {
			return input.id;
		}
	};

	private BallotIterableDiffer() {
	}

	public static <T extends Candidate> BallotIterableDiff calculateDiff(Iterable<Ballot<T>> ballotSetA,
	                                                                Iterable<Ballot<T>> ballotSetB) {
		Set<Long> setAsDuplicateIds = findDuplicateIds(ballotSetA);
		Set<Long> setBsDuplicateIds = findDuplicateIds(ballotSetB);

		Set<Long> inAButNotInB = inAButNotInB(ballotSetA, ballotSetB);
		Set<Long> inBButNotInA = inAButNotInB(ballotSetB, ballotSetA);

		Set<Long> differentBetweenTheTwoSets = differentBetweenSetAAndSetB(ballotSetA, ballotSetB);

		return new BallotIterableDiff(setAsDuplicateIds, setBsDuplicateIds, inAButNotInB, inBButNotInA,
		                         differentBetweenTheTwoSets);
	}


	private static <T extends Candidate> Set<Long> differentBetweenSetAAndSetB(Iterable<Ballot<T>> ballotSetA,
	                                                                           Iterable<Ballot<T>> ballotSetB) {
		Set<Long> idsOfDifferingBallots = new HashSet<>();

		Multimap<Long, Ballot<T>> idsToBallotSetMultimapForA = asIdToBallotsMulitmap(ballotSetA);
		Multimap<Long, Ballot<T>> idsToBallotSetMultimapForB = asIdToBallotsMulitmap(ballotSetB);
		for (Entry<Long, Collection<Ballot<T>>> entry : idsToBallotSetMultimapForA.asMap().entrySet()) {
			Collection<Ballot<T>> ballots = idsToBallotSetMultimapForB.get(entry.getKey());
			if ((entry.getValue().size() == 1) && (ballots.size() == 1)) { // Otherwise, it would have already been counted as non unique id
				if (!entry.getValue().iterator().next().equals(ballots.iterator().next())) {
					idsOfDifferingBallots.add(entry.getKey());
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
		public final ImmutableSet<Long> setAsDuplicateIds;
		public final ImmutableSet<Long> setBsDuplicateIds;
		public final ImmutableSet<Long> inAButNotInB;
		public final ImmutableSet<Long> inBButNotInA;
		public final ImmutableSet<Long> differentBetweenTheTwoSets;

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
	}
}
