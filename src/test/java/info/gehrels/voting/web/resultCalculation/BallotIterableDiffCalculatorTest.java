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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Candidate;
import info.gehrels.voting.Election;
import info.gehrels.voting.Vote;
import info.gehrels.voting.web.resultCalculation.BallotIterableDiffCalculator.BallotIterableDiff;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public final class BallotIterableDiffCalculatorTest {

	public static final ImmutableList<Ballot<Candidate>> EMPTY_BALLOT_LIST = ImmutableList.of();
	public static final Candidate PETER = new Candidate("Peter");
	public static final Candidate GUNDULA = new Candidate("Gundula");
	public static final Election<Candidate> TEST_ELECTION = new Election<>("test election",
	                                                                       ImmutableSet.of(PETER, GUNDULA));

	@Test
	public void calculatesEqualityBetweenEmptyBallotCollections() {
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffCalculator
			.calculateDiff(EMPTY_BALLOT_LIST, EMPTY_BALLOT_LIST);

		assertThat(ballotIterableDiff.isEqual(), is(true));
		assertThat(ballotIterableDiff.isDifferent(), is(false));
	}

	@Test
	public void calculatesEqualityBetweenTwoBallotCollectionsWithEqualContent() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		Ballot<Candidate> ballot2 = createBallot(5L, PETER);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffCalculator
			.calculateDiff(ImmutableList.of(ballot1), ImmutableList.of(ballot2));

		assertThat(ballotIterableDiff.isEqual(), is(true));
		assertThat(ballotIterableDiff.isDifferent(), is(false));
		assertThat(ballotIterableDiff.isBallotDifferentOrDuplicate(5), is(false));
	}

	@Test
	public void calculatesDifferenceIfFirstCollectionHasTwoBallotsWithTheSameId() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		Ballot<Candidate> ballot2 = createBallot(5L, GUNDULA);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffCalculator
			.calculateDiff(ImmutableList.of(ballot1, ballot2), ImmutableList.of());

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.getSetAsDuplicateIds(), contains(5L));
		assertThat(ballotIterableDiff.getSetBsDuplicateIds(), is(empty()));
		assertThat(ballotIterableDiff.getInAButNotInB(), is(empty()));
		assertThat(ballotIterableDiff.getInBButNotInA(), is(empty()));
		assertThat(ballotIterableDiff.getDifferentBetweenTheTwoSets(), is(empty()));
		assertThat(ballotIterableDiff.isBallotDifferentOrDuplicate(5), is(true));
	}

	@Test
	public void calculatesDifferenceIfSecondCollectionHasTwoBallotsWithTheSameId() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		Ballot<Candidate> ballot2 = createBallot(5L, GUNDULA);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffCalculator
			.calculateDiff(ImmutableList.of(ballot1), ImmutableList.of(ballot1, ballot2));

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.getSetAsDuplicateIds(), is(empty()));
		assertThat(ballotIterableDiff.getSetBsDuplicateIds(), contains(5L));
		assertThat(ballotIterableDiff.getInAButNotInB(), is(empty()));
		assertThat(ballotIterableDiff.getInBButNotInA(), is(empty()));
		assertThat(ballotIterableDiff.getDifferentBetweenTheTwoSets(), is(empty()));
		assertThat(ballotIterableDiff.isBallotDifferentOrDuplicate(5), is(true));
	}

	@Test
	public void calculatesDifferenceIfFirstCollectionHasAMemberThatDoesNotExistInTheSecondOne() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffCalculator
			.calculateDiff(ImmutableList.of(ballot1), ImmutableList.of());

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.getSetAsDuplicateIds(), is(empty()));
		assertThat(ballotIterableDiff.getSetBsDuplicateIds(), is(empty()));
		assertThat(ballotIterableDiff.getInAButNotInB(), contains(5L));
		assertThat(ballotIterableDiff.getInBButNotInA(), is(empty()));
		assertThat(ballotIterableDiff.getDifferentBetweenTheTwoSets(), is(empty()));
		assertThat(ballotIterableDiff.isBallotDifferentOrDuplicate(5), is(true));
	}

	@Test
	public void calculatesDifferenceIfSecondCollectionHasAMemberThatDoesNotExistInTheFirstOne() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffCalculator
			.calculateDiff(ImmutableList.of(), ImmutableList.of(ballot1));

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.getSetAsDuplicateIds(), is(empty()));
		assertThat(ballotIterableDiff.getSetBsDuplicateIds(), is(empty()));
		assertThat(ballotIterableDiff.getInAButNotInB(), is(empty()));
		assertThat(ballotIterableDiff.getInBButNotInA(), contains(5L));
		assertThat(ballotIterableDiff.getDifferentBetweenTheTwoSets(), is(empty()));
		assertThat(ballotIterableDiff.isBallotDifferentOrDuplicate(5), is(true));
	}

	@Test
	public void calculatesDifferenceIfBothCollectionsContainAMemberWithTheSameIdButDifferent() {
		Ballot<Candidate> ballot1 = createBallot(5L, PETER);
		Ballot<Candidate> ballot2 = createBallot(5L, GUNDULA);
		BallotIterableDiff ballotIterableDiff = BallotIterableDiffCalculator
			.calculateDiff(ImmutableList.of(ballot1), ImmutableList.of(ballot2));

		assertThat(ballotIterableDiff.isEqual(), is(false));
		assertThat(ballotIterableDiff.isDifferent(), is(true));
		assertThat(ballotIterableDiff.getSetAsDuplicateIds(), is(empty()));
		assertThat(ballotIterableDiff.getSetBsDuplicateIds(), is(empty()));
		assertThat(ballotIterableDiff.getInAButNotInB(), is(empty()));
		assertThat(ballotIterableDiff.getInBButNotInA(), is(empty()));
		assertThat(ballotIterableDiff.getDifferentBetweenTheTwoSets(), contains(5L));
		assertThat(ballotIterableDiff.isBallotDifferentOrDuplicate(5), is(true));
	}

	private Ballot<Candidate> createBallot(long id, Candidate... candidate) {
		return new Ballot<>(id, ImmutableSet.of(
			Vote.createPreferenceVote(TEST_ELECTION, ImmutableList.copyOf(candidate))));
	}


}
