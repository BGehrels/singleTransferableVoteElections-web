package info.gehrels.voting.web.applicationState;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.Ballot;
import info.gehrels.voting.Vote;
import info.gehrels.voting.genderedElections.GenderedCandidate;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BallotStateTest {

    private static final String OLD_OFFICE_NAME = "office";
    private static final GenderedCandidate CANDIDATE = new GenderedCandidate("Peter", true);
    private static final GenderedElection ORIGINAL_ELECTION = new GenderedElection(OLD_OFFICE_NAME, 1, 0, ImmutableSet.of(CANDIDATE));
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
    public void replaceElectionStoresNewElection() {
        BallotState ballotLayoutState = new BallotState();

        ballotLayoutState.setBallotLayout(ballotLayout);

        ballotLayoutState.replaceElectionVersion(OLD_OFFICE_NAME, (e) -> e.withOfficeName(NEW_OFFICE_NAME));

        assertThat(ballotLayoutState.getBallotLayout().getElections().get(0).getOfficeName(), is(NEW_OFFICE_NAME));
    }

    @Test
    public void replaceElectionMigratesCastBallots() {
        BallotState ballotLayoutState = new BallotState();
        ballotLayoutState.setBallotLayout(ballotLayout);
        ballotLayoutState.addCastBallot(BallotInputTry.FIRST, new Ballot<>(1L, ImmutableSet.of(Vote.createNoVote(ORIGINAL_ELECTION))));

        GenderedElection newChangedElection = ballotLayoutState.replaceElectionVersion(OLD_OFFICE_NAME, (e) -> e.withOfficeName(NEW_OFFICE_NAME));

        assertThat(ballotLayoutState.getFirstTryCastBallots().iterator().next().getVote(ORIGINAL_ELECTION).isPresent(), is(false));
        assertThat(ballotLayoutState.getFirstTryCastBallots().iterator().next().getVote(newChangedElection).get().getElection().getOfficeName(), is(NEW_OFFICE_NAME));
    }

    @Test()
    public void replaceElectionThrowsIfOldOfficeNameDoesNotExist() {
        BallotState ballotLayoutState = new BallotState();

        ballotLayoutState.setBallotLayout(ballotLayout);

        assertThrows(
                IllegalArgumentException.class,
                () -> ballotLayoutState.replaceElectionVersion("non existing Office Name", (e) -> e.withOfficeName(NEW_OFFICE_NAME))
        );
    }

    @Test
    public void replaceCandidateVersionStoresNewElection() {
        BallotState ballotLayoutState = new BallotState();

        ballotLayoutState.setBallotLayout(ballotLayout);

        boolean newIsFemale = !CANDIDATE.isFemale();
        ballotLayoutState.replaceCandidateVersion(OLD_OFFICE_NAME, CANDIDATE.getName(), (c) -> c.withIsFemale(newIsFemale));

        assertThat(ballotLayoutState.getBallotLayout().getElections().get(0).getCandidate(CANDIDATE.getName()).get().isFemale(), is(newIsFemale));
    }

    @Test
    public void replaceCandidateVersionMigratesCastBallots() {
        BallotState ballotLayoutState = new BallotState();
        ballotLayoutState.setBallotLayout(ballotLayout);
        ballotLayoutState.addCastBallot(BallotInputTry.FIRST, new Ballot<>(1L, ImmutableSet.of(Vote.createPreferenceVote(ORIGINAL_ELECTION, ImmutableList.of(CANDIDATE)))));

        boolean newIsFemale = !CANDIDATE.isFemale();
        ballotLayoutState.replaceCandidateVersion(OLD_OFFICE_NAME, CANDIDATE.getName(), (c) -> c.withIsFemale(newIsFemale));

        assertThat(ballotLayoutState.getFirstTryCastBallots().iterator().next().getVote(ORIGINAL_ELECTION).isPresent(), is(false));
        assertThat(ballotLayoutState.getFirstTryCastBallots().iterator().next().getVote(ORIGINAL_ELECTION.getOfficeName()).get().getElection().getCandidate(CANDIDATE.getName()).get().isFemale(), is(newIsFemale));
    }

    @Test()
    public void replaceCandidateVersionThrowsIfOldOfficeNameDoesNotExist() {
        BallotState ballotLayoutState = new BallotState();

        ballotLayoutState.setBallotLayout(ballotLayout);

        assertThrows(
                IllegalArgumentException.class,
                () -> ballotLayoutState.replaceCandidateVersion("non existing Office Name", CANDIDATE.getName(), (c) -> c.withIsFemale(false))
        );
    }

    @Test()
    public void replaceCandidateVersionThrowsIfCandidateNameDoesNotExist() {
        BallotState ballotLayoutState = new BallotState();

        ballotLayoutState.setBallotLayout(ballotLayout);

        assertThrows(
                IllegalArgumentException.class,
                () -> ballotLayoutState.replaceCandidateVersion(OLD_OFFICE_NAME, "Mustermann", (c) -> c.withIsFemale(false))
        );
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