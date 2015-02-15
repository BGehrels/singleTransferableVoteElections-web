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

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public final class HtmlUnitIT {
    public static final String CANDIDATE_NAME = "The Candidate";
    public static final String OFFICE_NAME = "The Office";
    @Value("${local.server.port}")
    int port;
    private WebDriver driver;

    @Before
    public void setUp() {
        driver = new HtmlUnitDriver();
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    public void singleElectionSingleCandidateSingleVoteWalkthrough() throws MalformedURLException {
        driver.navigate().to(new URL("http", "localhost", port, "/"));

        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);
        AdministrateBallotLayoutPage administrateBallotLayoutPage = indexPage.clickAdministrateBallotLayoutLink();
        administrateBallotLayoutPage.setOfficeName(0, OFFICE_NAME);
        administrateBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 1);
        administrateBallotLayoutPage.setNumberOfNonFemaleExclusivePositions(0, 1);
        administrateBallotLayoutPage.setCandidateName(0, 0, CANDIDATE_NAME);
        administrateBallotLayoutPage.setCandidateFemale(0, 0, true);
        indexPage = administrateBallotLayoutPage.clickBallotLayoutCompleted();
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage.setBallotId(1);
        castVotePage.setVoteType(0, VoteType.PREFERENCE);
        castVotePage.setPreference(0, 0, 1);
        castVotePage = castVotePage.clickCastVote();
        indexPage = castVotePage.clickBackToIndePage();
        castVotePage = indexPage.clickCastVotesSecondTryLink();
        castVotePage.setBallotId(1);
        castVotePage.setVoteType(0, VoteType.PREFERENCE);
        castVotePage.setPreference(0,0,1);
        castVotePage.clickCastVote();
        indexPage = castVotePage.clickBackToIndePage();

        ManageElectionCalculationsPage manageElectionCalculationsPage = indexPage.clickElectionCalculationLink();
        manageElectionCalculationsPage = manageElectionCalculationsPage.clickStartNewElectionCalculation();
        ElectionCalculationPage electionCalculationPage = manageElectionCalculationsPage.clickElectionCalculation();
        while (!electionCalculationPage.electionCalculatinHasFinished()) {
            electionCalculationPage = electionCalculationPage.refresh();
        }

        assertThat(electionCalculationPage.getElectedFemaleCandidateNames(OFFICE_NAME), contains(CANDIDATE_NAME));

    }
}
