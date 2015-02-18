package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.VoteType;
import info.gehrels.voting.web.integrationTests.pages.AdministrateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.DifferingBallotsPage;
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
public final class HandleDifferingBallotsIT {
    public static final String CANDIDATE_NAME_1 = "The first candidate";
    public static final String CANDIDATE_NAME_2 = "The second candidate";
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
    public void handleDifferingBallots() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);
        AdministrateBallotLayoutPage administrateBallotLayoutPage = indexPage.clickAdministrateBallotLayoutLink();

        administrateBallotLayoutPage.setOfficeName(0, OFFICE_NAME);
        administrateBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 0);
        administrateBallotLayoutPage.setNumberOfNonFemaleExclusivePositions(0, 1);

        administrateBallotLayoutPage.setCandidateName(0, 0, CANDIDATE_NAME_1);
        administrateBallotLayoutPage.setCandidateFemale(0, 0, true);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);

        administrateBallotLayoutPage.setCandidateName(0, 1, CANDIDATE_NAME_2);
        administrateBallotLayoutPage.setCandidateFemale(0, 1, false);

        indexPage = administrateBallotLayoutPage.clickBallotLayoutCompleted();
        indexPage = enterFirstTryDuplicateAndSecondTryUniqueVote(indexPage, 5);
        indexPage = enterSecondTryDuplicateAndFirstTryUniqueVote(indexPage, 4);
        indexPage = enterVoteThatIsOnlyInFirstTry(indexPage, 3);
        indexPage = enterVoteThatIsOnlyInSecondTry(indexPage, 2);
        indexPage = enterVoteThatIsDifferingBetweenFirstAndSecondTry(indexPage, 1);

        DifferingBallotsPage differingBallotsPage = indexPage
                .clickElectionCalculationLink()
                .clickStartNewElectionCalculation(DifferingBallotsPage.class);
        assertThat(differingBallotsPage.getFirstTryDuplicates(), contains(5));
        assertThat(differingBallotsPage.getSecondTryDuplicates(), contains(4));
        assertThat(differingBallotsPage.getOnlyInFirstTry(), contains(3));
        assertThat(differingBallotsPage.getOnlyInSecondTry(), contains(2));
        assertThat(differingBallotsPage.getDifferentBetweenFirstAndSecondTry(), contains(1));

        indexPage = differingBallotsPage.clickDeleteAllConflictingBallots();
        indexPage
                .clickElectionCalculationLink()
                .clickStartNewElectionCalculation(ManageElectionCalculationsPage.class)
                .clickElectionCalculation();
    }

    private IndexPage enterVoteThatIsDifferingBetweenFirstAndSecondTry(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage.setBallotId(1);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.NOT_VOTED);
        castVotePage = castVotePage.clickCastVote().clickBackToIndexPage().clickCastVotesSecondTryLink();
        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_1, 2);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_2, 1);
        return castVotePage
                .clickCastVote()
                .clickBackToIndexPage();
    }

    private IndexPage enterVoteThatIsOnlyInSecondTry(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesSecondTryLink();
        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.NO);
        return castVotePage
                .clickCastVote()
                .clickBackToIndexPage();
    }

    private IndexPage enterVoteThatIsOnlyInFirstTry(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.INVALID);
        return castVotePage
                .clickCastVote()
                .clickBackToIndexPage();
    }

    private IndexPage enterSecondTryDuplicateAndFirstTryUniqueVote(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_1, 1);

        castVotePage = castVotePage.clickCastVote()
                .clickBackToIndexPage()
                .clickCastVotesSecondTryLink();

        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_1, 1);
        castVotePage = castVotePage.clickCastVote();

        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_1, 1);
        return castVotePage
                .clickCastVote()
                .clickBackToIndexPage();
    }

    private IndexPage enterFirstTryDuplicateAndSecondTryUniqueVote(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_2, 1);
        castVotePage = castVotePage.clickCastVote();

        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_2, 1);

        castVotePage = castVotePage.clickCastVote()
                .clickBackToIndexPage()
                .clickCastVotesSecondTryLink();

        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_2, 1);
        return castVotePage
                .clickCastVote()
                .clickBackToIndexPage();
    }
}
