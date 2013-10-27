package info.gehrels.voting.web;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Candidate;
import info.gehrels.voting.Election;
import info.gehrels.voting.Vote;
import info.gehrels.voting.web.BallotIterableDiffer.BallotIterableDiff;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public final class BallotIterableDifferTest {

	public static final ImmutableList<Ballot<Candidate>> EMPTY_BALLOT_LIST = ImmutableList.of();
	public static final Candidate PETER = new Candidate("Peter");
	public static final Candidate GUNDULA = new Candidate("Gundula");
	public static final Election<Candidate> TEST_ELECTION = new Election<>("test election",
	                                                                       ImmutableSet.of(PETER, GUNDULA));

	@Test
	public void calculatesEqualityBetweenEmptyBallotCollections() {
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffer.calculateDiff(EMPTY_BALLOT_LIST, EMPTY_BALLOT_LIST);

		assertThat(ballotIterableDiff.isEqual(), is(true));
		assertThat(ballotIterableDiff.isDifferent(), is(false));
	}

	@Test
	public void calculatesEqualityBetweenTwoBallotCollectionsWithEqualContent() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		Ballot<Candidate> ballot2 = createBallot(5L, PETER);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffer
			.calculateDiff(ImmutableList.of(ballot1), ImmutableList.of(ballot2));

		assertThat(ballotIterableDiff.isEqual(), is(true));
		assertThat(ballotIterableDiff.isDifferent(), is(false));
	}

	@Test
	public void calculatesDifferenceIfFirstCollectionHasTwoBallotsWithTheSameId() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		Ballot<Candidate> ballot2 = createBallot(5L, GUNDULA);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffer
			.calculateDiff(ImmutableList.of(ballot1, ballot2), ImmutableList.of(ballot2));

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.setAsDuplicateIds, contains(5L));
		assertThat(ballotIterableDiff.setBsDuplicateIds, is(empty()));
		assertThat(ballotIterableDiff.inAButNotInB, is(empty()));
		assertThat(ballotIterableDiff.inBButNotInA, is(empty()));
		assertThat(ballotIterableDiff.differentBetweenTheTwoSets, is(empty()));
	}

	@Test
	public void calculatesDifferenceIfSecondCollectionHasTwoBallotsWithTheSameId() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		Ballot<Candidate> ballot2 = createBallot(5L, GUNDULA);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffer
			.calculateDiff(ImmutableList.of(ballot1), ImmutableList.of(ballot1, ballot2));

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.setAsDuplicateIds, is(empty()));
		assertThat(ballotIterableDiff.setBsDuplicateIds, contains(5L));
		assertThat(ballotIterableDiff.inAButNotInB, is(empty()));
		assertThat(ballotIterableDiff.inBButNotInA, is(empty()));
		assertThat(ballotIterableDiff.differentBetweenTheTwoSets, is(empty()));
	}

	@Test
	public void calculatesDifferenceIfFirstCollectionHasAMemberThatDoesNotExistInTheSecondOne() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffer
			.calculateDiff(ImmutableList.of(ballot1), ImmutableList.<Ballot<Candidate>>of());

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.setAsDuplicateIds, is(empty()));
		assertThat(ballotIterableDiff.setBsDuplicateIds, is(empty()));
		assertThat(ballotIterableDiff.inAButNotInB, contains(5L));
		assertThat(ballotIterableDiff.inBButNotInA, is(empty()));
		assertThat(ballotIterableDiff.differentBetweenTheTwoSets, is(empty()));
	}

	@Test
	public void calculatesDifferenceIfSecondCollectionHasAMemberThatDoesNotExistInTheFirstOne() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffer
			.calculateDiff(ImmutableList.<Ballot<Candidate>>of(), ImmutableList.of(ballot1));

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.setAsDuplicateIds, is(empty()));
		assertThat(ballotIterableDiff.setBsDuplicateIds, is(empty()));
		assertThat(ballotIterableDiff.inAButNotInB, is(empty()));
		assertThat(ballotIterableDiff.inBButNotInA, contains(5L));
		assertThat(ballotIterableDiff.differentBetweenTheTwoSets, is(empty()));
	}

	@Test
	public void calculatesDifferenceIfBothCollectionsContainAMemberWithTheSameIdButDifferent() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		Ballot<Candidate> ballot2 = createBallot(5L, GUNDULA);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffer
			.calculateDiff(ImmutableList.of(ballot1), ImmutableList.of(ballot2));

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.setAsDuplicateIds, is(empty()));
		assertThat(ballotIterableDiff.setBsDuplicateIds, is(empty()));
		assertThat(ballotIterableDiff.inAButNotInB, is(empty()));
		assertThat(ballotIterableDiff.inBButNotInA, is(empty()));
		assertThat(ballotIterableDiff.differentBetweenTheTwoSets, contains(5L));
	}

	private Ballot<Candidate> createBallot(long id, Candidate... candidate) {
		return new Ballot<>(id, ImmutableSet.of(
			Vote.createPreferenceVote(TEST_ELECTION, ImmutableSet.<Candidate>copyOf(candidate))));
	}


}
