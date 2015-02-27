package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.VoteType;
import info.gehrels.voting.web.integrationTests.pages.AdministrateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.ElectionCalculationPage;
import info.gehrels.voting.web.integrationTests.pages.IndexPage;
import info.gehrels.voting.web.integrationTests.pages.ManageElectionCalculationsPage;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
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
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public final class AmbigiuityResolutionIT {
    public static final String CANDIDATE_NAME_1 = "The first candidate";
    public static final String CANDIDATE_NAME_2 = "The second candidate";
    public static final String OFFICE_NAME = "The Office";
    public static final String AMBIGUITY_RESOLUTION_DESCRIPTION = "Ambiguity resolution description";

    @Value("${local.server.port}")
    private int port;

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
    public void createAmbigiousSituatinAndResolveIt() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        AdministrateBallotLayoutPage administrateBallotLayoutPage = indexPage.clickAdministrateBallotLayoutLink();
        administrateBallotLayoutPage.setOfficeName(0, OFFICE_NAME);
        administrateBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 0);
        administrateBallotLayoutPage.setNumberOfNonFemaleExclusivePositions(0, 1);
        administrateBallotLayoutPage.setCandidateName(0, 0, CANDIDATE_NAME_1);
        administrateBallotLayoutPage.setCandidateFemale(0, 0, false);
        administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage.setCandidateName(0, 1, CANDIDATE_NAME_2);
        administrateBallotLayoutPage.setCandidateFemale(0, 1, false);
        indexPage = administrateBallotLayoutPage.clickBallotLayoutCompleted();

        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage = castVote(castVotePage, 1, 1, 2);
        castVotePage = castVotePage.clickBackToIndexPage().clickCastVotesSecondTryLink();
        castVotePage = castVote(castVotePage, 1, 1, 2);

        castVotePage = castVote(castVotePage, 2, 2, 1);
        castVotePage = castVotePage.clickBackToIndexPage().clickCastVotesFirstTryLink();
        castVotePage = castVote(castVotePage, 2, 2, 1);

        ElectionCalculationPage electionCalculationPage =
                castVotePage
                        .clickBackToIndexPage()
                        .clickElectionCalculationLink()
                        .clickStartNewElectionCalculation(ManageElectionCalculationsPage.class)
                        .clickElectionCalculation();

        electionCalculationPage = electionCalculationPage.waitUntilAmbiguityResolutionIsNeccessary();
        electionCalculationPage.chooseCandidate(CANDIDATE_NAME_2);
        electionCalculationPage.setDescription(AMBIGUITY_RESOLUTION_DESCRIPTION);
        electionCalculationPage = electionCalculationPage.submitAmbiguityResolution();

        electionCalculationPage.waitForElectionCalculationToBeFinished();
        assertThat(electionCalculationPage.getWinningNonFemaleExclusiveCandidate(OFFICE_NAME), contains(CANDIDATE_NAME_1));
        assertThat(electionCalculationPage.getProtocol(OFFICE_NAME), containsString(AMBIGUITY_RESOLUTION_DESCRIPTION));
    }

    private CastVotePage castVote(CastVotePage castVotePage, int ballotId, int candidateOnePreference, int candidateTwoPreference) {
        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_1, candidateOnePreference);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_2, candidateTwoPreference);
        return castVotePage.clickCastVote();
    }
}
