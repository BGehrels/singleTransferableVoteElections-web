package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.ballotCasting.VoteType;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.CreateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.ElectionCalculationPage;
import info.gehrels.voting.web.integrationTests.pages.IndexPage;
import info.gehrels.voting.web.integrationTests.pages.ManageElectionCalculationsPage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public final class HappyPathIT {
    private static final String CANDIDATE_NAME_1 = "The Candidate";
    private static final String CANDIDATE_NAME_2 = "The second Candidate";
    private static final String CANDIDATE_NAME_3 = "The third candidate";
    private static final String OFFICE_NAME_1 = "The Office";
    private static final String OFFICE_NAME_2 = "The second office";

    @Value("${local.server.port}")
    int port;

    @Rule
    public WebDriverRule webDriverRule = new WebDriverRule();

    @Before
    public void setUp() throws MalformedURLException {
        webDriverRule.getDriver().navigate().to(new URL("http", "localhost", port, "/"));
    }

    @Test
    public void twoElectionsDifferentCandidatesWalkThrough() {
        IndexPage indexPage = PageFactory.initElements(webDriverRule.getDriver(), IndexPage.class);

        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage = createFirstElection(createBallotLayoutPage);
        createBallotLayoutPage.clickAddElection();
        createSecondElection(createBallotLayoutPage);

        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();

        indexPage = castSomeVotesOfEachType(indexPage.clickCastVotesFirstTryLink()).clickBackToIndexPage();
        indexPage = castSomeVotesOfEachType(indexPage.clickCastVotesSecondTryLink()).clickBackToIndexPage();

        ManageElectionCalculationsPage manageElectionCalculationsPage = indexPage.clickElectionCalculationLink();
        manageElectionCalculationsPage = manageElectionCalculationsPage.clickStartNewElectionCalculation(ManageElectionCalculationsPage.class);
        ElectionCalculationPage electionCalculationPage = manageElectionCalculationsPage.clickElectionCalculation();
        electionCalculationPage = electionCalculationPage.waitForElectionCalculationToBeFinished();

        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(OFFICE_NAME_1), contains(CANDIDATE_NAME_1));
        assertThat(electionCalculationPage.getNotFemaleExclusiveElectedCandidateNames(OFFICE_NAME_1), contains(CANDIDATE_NAME_2));
        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(OFFICE_NAME_2), is(empty()));
        assertThat(electionCalculationPage.getNotFemaleExclusiveElectedCandidateNames(OFFICE_NAME_2), contains(CANDIDATE_NAME_3));
    }

    private CreateBallotLayoutPage createFirstElection(CreateBallotLayoutPage createBallotLayoutPage) {
        createBallotLayoutPage.setOfficeName(0, OFFICE_NAME_1);
        createBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 1);
        createBallotLayoutPage.setNumberOfNotFemaleExclusivePositions(0, 1);
        createBallotLayoutPage.setCandidateName(0, 0, CANDIDATE_NAME_1);
        createBallotLayoutPage.setCandidateFemale(0, 0, true);
        createBallotLayoutPage = createBallotLayoutPage.clickAddCandidate(0);
        createBallotLayoutPage.setCandidateName(0, 1, CANDIDATE_NAME_2);
        createBallotLayoutPage.setCandidateFemale(0, 1, false);
        return createBallotLayoutPage;
    }

    private void createSecondElection(CreateBallotLayoutPage createBallotLayoutPage) {
        createBallotLayoutPage.setOfficeName(1, OFFICE_NAME_2);
        createBallotLayoutPage.setNumberOfFemaleExclusivePositions(1, 0);
        createBallotLayoutPage.setNumberOfNotFemaleExclusivePositions(1, 1);
        createBallotLayoutPage.setCandidateName(1, 0, CANDIDATE_NAME_3);
        createBallotLayoutPage.setCandidateFemale(1, 0, true);
    }

    private CastVotePage castSomeVotesOfEachType(CastVotePage castVotePage) {
        castVotePage = castVotePage.setBallotId(1).setPreferences(OFFICE_NAME_1, CANDIDATE_NAME_1).setPreferences(OFFICE_NAME_2, CANDIDATE_NAME_3).clickCastVote();
        castVotePage = castVotePage.setBallotId(2).setPreferences(OFFICE_NAME_1, CANDIDATE_NAME_2).setPreferences(OFFICE_NAME_2, CANDIDATE_NAME_3).clickCastVote();
        castVotePage = castVotePage.setBallotId(3).setPreferences(OFFICE_NAME_1, CANDIDATE_NAME_1, CANDIDATE_NAME_2).setVoteType(OFFICE_NAME_2, VoteType.NO).clickCastVote();
        castVotePage = castVotePage.setBallotId(4).setPreferences(OFFICE_NAME_1, CANDIDATE_NAME_1, CANDIDATE_NAME_2).setVoteType(OFFICE_NAME_2, VoteType.INVALID).clickCastVote();
        castVotePage = castVotePage.setBallotId(5).setPreferences(OFFICE_NAME_1, CANDIDATE_NAME_2, CANDIDATE_NAME_1).setVoteType(OFFICE_NAME_2, VoteType.NOT_VOTED).clickCastVote();
        castVotePage = castVotePage.setBallotId(6).setPreferences(OFFICE_NAME_1, CANDIDATE_NAME_1, CANDIDATE_NAME_2).setPreferences(OFFICE_NAME_2, CANDIDATE_NAME_3).clickCastVote();;
        castVotePage = castVotePage.setBallotId(7).setVoteType(OFFICE_NAME_1, VoteType.NOT_VOTED).setPreferences(OFFICE_NAME_2, CANDIDATE_NAME_3).clickCastVote();;
        castVotePage = castVotePage.setBallotId(8).setVoteType(OFFICE_NAME_1, VoteType.NO).setPreferences(OFFICE_NAME_2, CANDIDATE_NAME_3).clickCastVote();;
        castVotePage = castVotePage.setBallotId(9).setVoteType(OFFICE_NAME_1, VoteType.INVALID).setPreferences(OFFICE_NAME_2, CANDIDATE_NAME_3).clickCastVote();;

        return castVotePage;
    }
}
