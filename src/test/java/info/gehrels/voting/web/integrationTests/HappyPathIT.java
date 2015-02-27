package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.VoteType;
import info.gehrels.voting.web.integrationTests.pages.AdministrateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.ElectionCalculationPage;
import info.gehrels.voting.web.integrationTests.pages.IndexPage;
import info.gehrels.voting.web.integrationTests.pages.ManageElectionCalculationsPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public final class HappyPathIT {
    public static final String CANDIDATE_NAME_1 = "The Candidate";
    public static final String CANDIDATE_NAME_2 = "The second Candidate";
    public static final String OFFICE_NAME = "The Office";
    @Value("${local.server.port}")
    int port;
    private WebDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        driver = new FirefoxDriver();
        driver.navigate().to(new URL("http", "localhost", port, "/"));
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    public void singleElectionSingleCandidateSingleVoteWalkthrough() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        AdministrateBallotLayoutPage administrateBallotLayoutPage = indexPage.clickAdministrateBallotLayoutLink();
        administrateBallotLayoutPage.setOfficeName(0, OFFICE_NAME);
        administrateBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 1);
        administrateBallotLayoutPage.setNumberOfNonFemaleExclusivePositions(0, 1);
        administrateBallotLayoutPage.setCandidateName(0, 0, CANDIDATE_NAME_1);
        administrateBallotLayoutPage.setCandidateFemale(0, 0, true);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage.setCandidateName(0, 1, CANDIDATE_NAME_2);
        administrateBallotLayoutPage.setCandidateFemale(0, 1, false);

        indexPage = administrateBallotLayoutPage.clickBallotLayoutCompleted();

        indexPage = castSomeVotesOfEachType(indexPage.clickCastVotesFirstTryLink()).clickBackToIndexPage();
        indexPage = castSomeVotesOfEachType(indexPage.clickCastVotesSecondTryLink()).clickBackToIndexPage();

        ManageElectionCalculationsPage manageElectionCalculationsPage = indexPage.clickElectionCalculationLink();
        manageElectionCalculationsPage = manageElectionCalculationsPage.clickStartNewElectionCalculation(ManageElectionCalculationsPage.class);
        ElectionCalculationPage electionCalculationPage = manageElectionCalculationsPage.clickElectionCalculation();
        electionCalculationPage = electionCalculationPage.waitForElectionCalculationToBeFinished();

        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(OFFICE_NAME), contains(CANDIDATE_NAME_1));
            assertThat(electionCalculationPage.getNonFemaleExclusiveElectedCandidateNames(OFFICE_NAME), contains(CANDIDATE_NAME_2));
    }

    private CastVotePage castSomeVotesOfEachType(CastVotePage castVotePage) {
        castVotePage = castVotePage.castPreferenceVote(1, OFFICE_NAME, CANDIDATE_NAME_1);
        castVotePage = castVotePage.castPreferenceVote(2, OFFICE_NAME, CANDIDATE_NAME_2);
        castVotePage = castVotePage.castPreferenceVote(3, OFFICE_NAME, CANDIDATE_NAME_1, CANDIDATE_NAME_2);
        castVotePage = castVotePage.castPreferenceVote(4, OFFICE_NAME, CANDIDATE_NAME_1, CANDIDATE_NAME_2);
        castVotePage = castVotePage.castPreferenceVote(5, OFFICE_NAME, CANDIDATE_NAME_2, CANDIDATE_NAME_1);
        castVotePage = castVotePage.castPreferenceVote(6, OFFICE_NAME, CANDIDATE_NAME_1, CANDIDATE_NAME_2);
        castVotePage = castVotePage.castNonPreferenceVote(7, OFFICE_NAME, VoteType.NOT_VOTED);
        castVotePage = castVotePage.castNonPreferenceVote(8, OFFICE_NAME, VoteType.NO);
        castVotePage = castVotePage.castNonPreferenceVote(9, OFFICE_NAME, VoteType.INVALID);

        return castVotePage;
    }
}
