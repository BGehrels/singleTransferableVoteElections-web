package info.gehrels.voting.web.applicationState;


import com.google.common.collect.ImmutableSet;
import info.gehrels.voting.genderedElections.GenderedElection;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BallotLayoutTest {

    private static final GenderedElection OFFICE_1 = new GenderedElection("Office1", 1, 1, ImmutableSet.of());
    private static final GenderedElection OFFICE_2 = new GenderedElection("Office2", 2, 2, ImmutableSet.of());

    @Test
    public void replaceElectionReplacesOnlyTheElectionGivenByName() {
        BallotLayout ballotLayout = new BallotLayout(asList(OFFICE_1, OFFICE_2));
        ballotLayout.replaceElection(OFFICE_1.getOfficeName(), o -> o.withOfficeName("Blubb"));

        assertThat(ballotLayout.getElection(OFFICE_1.getOfficeName()).isPresent(), is(false));
        assertThat(ballotLayout.getElection("Blubb").isPresent(), is(true));
        assertThat(ballotLayout.getElection(OFFICE_2.getOfficeName()).isPresent(), is(true));
        assertThat(ballotLayout.getElection(OFFICE_2.getOfficeName()).get(), is(OFFICE_2));
    }
}