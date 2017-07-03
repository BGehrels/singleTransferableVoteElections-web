package info.gehrels.voting.web.applicationState;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BallotLayoutStateTest {

    private static final String OLD_OFFICE_NAME = "office";
    private static final BallotLayout BALLOT_LAYOUT = new BallotLayout(ImmutableList.of(new GenderedElection("office", 1, 0, ImmutableSet.of(new GenderedCandidate("Peter", true)))));
    private static final String NEW_OFFICE_NAME = "new Office";

    @Test
    public void initializedWithoutLayout() {
        assertThat(new BallotLayoutState().isBallotLayoutPresent(), is(false));
    }

    @Test
    public void hasLayoutAfterSettingOne() {
        BallotLayoutState ballotLayoutState = new BallotLayoutState();

        ballotLayoutState.setBallotLayout(BALLOT_LAYOUT);

        assertThat(ballotLayoutState.isBallotLayoutPresent(), is(true));
        assertThat(ballotLayoutState.getBallotLayout(), is(BALLOT_LAYOUT));
    }

    @Test
    public void storesNewElectionAfterCallingChangeOfficeName() {
        BallotLayoutState ballotLayoutState = new BallotLayoutState();

        ballotLayoutState.setBallotLayout(BALLOT_LAYOUT);

        ballotLayoutState.changeOfficeName(OLD_OFFICE_NAME, NEW_OFFICE_NAME);

        assertThat(ballotLayoutState.getBallotLayout().getElections().get(0).getOfficeName(), is(NEW_OFFICE_NAME));
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsIfOldOfficeNameDoesNotExist() {
        BallotLayoutState ballotLayoutState = new BallotLayoutState();

        ballotLayoutState.setBallotLayout(BALLOT_LAYOUT);

        ballotLayoutState.changeOfficeName("non existing Office Name", NEW_OFFICE_NAME);

        assertThat(ballotLayoutState.getBallotLayout().getElections().get(0).getOfficeName(), is(OLD_OFFICE_NAME));
    }



}