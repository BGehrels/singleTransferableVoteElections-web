package info.gehrels.voting.web.applicationState;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class BallotStateTest {

    private static final String OLD_OFFICE_NAME = "office";
    private static final GenderedElection ORIGINAL_ELECTION = new GenderedElection(OLD_OFFICE_NAME, 1, 0, ImmutableSet.of(new GenderedCandidate("Peter", true)));
    private static final String NEW_OFFICE_NAME = "new Office";

    private final BallotLayout ballotLayout = new BallotLayout(ImmutableList.of(ORIGINAL_ELECTION));

    @Test
    public void initializedWithoutLayout() {
        assertThat(new BallotState().isBallotLayoutPresent(), is(false));
    }

    @Test
    public void hasLayoutAfterSettingOne() {
        BallotState ballotLayoutState = new BallotState();

        ballotLayoutState.setBallotLayout(ballotLayout);

        assertThat(ballotLayoutState.isBallotLayoutPresent(), is(true));
        assertThat(ballotLayoutState.getBallotLayout(), is(ballotLayout));
    }

    @Test
    public void changeOfficeNameStoresNewElection() {
        BallotState ballotLayoutState = new BallotState();

        ballotLayoutState.setBallotLayout(ballotLayout);

        ballotLayoutState.changeOfficeName(OLD_OFFICE_NAME, NEW_OFFICE_NAME);

        assertThat(ballotLayoutState.getBallotLayout().getElections().get(0).getOfficeName(), is(NEW_OFFICE_NAME));
    }

    @Test
    public void changeOfficeNameMigratesCastBallots() {
        BallotState ballotLayoutState = new BallotState();
        ballotLayoutState.setBallotLayout(ballotLayout);
        ballotLayoutState.addCastBallot(BallotInputTry.FIRST, new Ballot<>(1L, ImmutableSet.of(Vote.createNoVote(ORIGINAL_ELECTION))));

        GenderedElection newChangedElection = ballotLayoutState.changeOfficeName(OLD_OFFICE_NAME, NEW_OFFICE_NAME);

        assertThat(ballotLayoutState.getFirstTryCastBallots().iterator().next().getVote(ORIGINAL_ELECTION).isPresent(), is(false));
        assertThat(ballotLayoutState.getFirstTryCastBallots().iterator().next().getVote(newChangedElection).get().getElection().getOfficeName(), is(NEW_OFFICE_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIfOldOfficeNameDoesNotExist() {
        BallotState ballotLayoutState = new BallotState();

        ballotLayoutState.setBallotLayout(ballotLayout);

        ballotLayoutState.changeOfficeName("non existing Office Name", NEW_OFFICE_NAME);

        assertThat(ballotLayoutState.getBallotLayout().getElections().get(0).getOfficeName(), is(OLD_OFFICE_NAME));
    }

    @Test
    public void setBallotLayoutResetsCastBallots() {
        BallotState ballotLayoutState = new BallotState();
        ballotLayoutState.setBallotLayout(ballotLayout);
        ballotLayoutState.addCastBallot(BallotInputTry.FIRST, new Ballot<>(1L, ImmutableSet.of(Vote.createNoVote(ORIGINAL_ELECTION))));

        ballotLayoutState.setBallotLayout(ballotLayout);

        assertThat(ballotLayoutState.getFirstTryCastBallots(), is(empty()));
        assertThat(ballotLayoutState.getFirstTryCastBallots(), is(empty()));
    }



}