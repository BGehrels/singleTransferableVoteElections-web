package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.VoteType;
import info.gehrels.voting.web.integrationTests.pages.*;
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
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;

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
        indexPage = enterFirstTryDuplicateAndSecondTryUniqueVote(indexPage, 1);
        indexPage = enterSecondTryDuplicateAndFirstTryUniqueVote(indexPage, 2);
        indexPage = enterVoteThatIsOnlyInFirstTry(indexPage, 3);
        indexPage = enterVoteThatIsOnlyInSecondTry(indexPage, 4);
        indexPage = enterVoteThatIsDifferingBetweenFirstAndSecondTry(indexPage, 5);
        indexPage = enterVoteThatIsWithoutAnyProblemsAndVotesFor(indexPage, 6, CANDIDATE_NAME_1);

        DifferingBallotsPage differingBallotsPage = indexPage
                .clickElectionCalculationLink()
                .clickStartNewElectionCalculation(DifferingBallotsPage.class);
        assertThat(differingBallotsPage.getFirstTryDuplicates(), contains(1));
        assertThat(differingBallotsPage.getSecondTryDuplicates(), contains(2));
        assertThat(differingBallotsPage.getOnlyInFirstTry(), contains(3));
        assertThat(differingBallotsPage.getOnlyInSecondTry(), contains(4));
        assertThat(differingBallotsPage.getDifferentBetweenFirstAndSecondTry(), contains(5));

        indexPage = differingBallotsPage.clickDeleteAllConflictingBallots();
        ElectionCalculationPage electionCalculationPage = indexPage
                .clickElectionCalculationLink()
                .clickStartNewElectionCalculation(ManageElectionCalculationsPage.class)
                .clickElectionCalculation();

        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(OFFICE_NAME), is(empty()));
        assertThat(electionCalculationPage.getNonFemaleExclusiveElectedCandidateNames(OFFICE_NAME), contains(CANDIDATE_NAME_1));
    }

    private IndexPage enterVoteThatIsWithoutAnyProblemsAndVotesFor(IndexPage indexPage, int ballotId, String candidateName) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage = castVotePage.castPreferenceVote(ballotId, OFFICE_NAME, candidateName);
        castVotePage = castVotePage.clickBackToIndexPage().clickCastVotesSecondTryLink();
        castVotePage = castVotePage.castPreferenceVote(ballotId, OFFICE_NAME, candidateName);
        return castVotePage.clickBackToIndexPage();
    }

    private IndexPage enterVoteThatIsDifferingBetweenFirstAndSecondTry(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage = castVotePage.castNonPreferenceVote(ballotId, OFFICE_NAME, VoteType.NOT_VOTED);
        castVotePage = castVotePage.clickBackToIndexPage().clickCastVotesSecondTryLink();
        castVotePage.setBallotId(ballotId);
        castVotePage.setVoteType(OFFICE_NAME, VoteType.PREFERENCE);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_1, 2);
        castVotePage.setPreference(OFFICE_NAME, CANDIDATE_NAME_2, 1);
        return castVotePage.clickCastVote().clickBackToIndexPage();
    }

    private IndexPage enterVoteThatIsOnlyInSecondTry(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesSecondTryLink();
        castVotePage = castVotePage.castNonPreferenceVote(ballotId, OFFICE_NAME, VoteType.NO);
        return castVotePage.clickBackToIndexPage();
    }

    private IndexPage enterVoteThatIsOnlyInFirstTry(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage = castVotePage.castNonPreferenceVote(ballotId, OFFICE_NAME, VoteType.INVALID);
        return castVotePage
                .clickBackToIndexPage();
    }

    private IndexPage enterSecondTryDuplicateAndFirstTryUniqueVote(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        castVotePage = castVotePage.castPreferenceVote(ballotId, OFFICE_NAME, CANDIDATE_NAME_1);

        castVotePage = castVotePage.clickBackToIndexPage().clickCastVotesSecondTryLink();

        castVotePage = castVotePage.castPreferenceVote(ballotId, OFFICE_NAME, CANDIDATE_NAME_1);
        castVotePage = castVotePage.castPreferenceVote(ballotId, OFFICE_NAME, CANDIDATE_NAME_1);

        return castVotePage.clickBackToIndexPage();
    }

    private IndexPage enterFirstTryDuplicateAndSecondTryUniqueVote(IndexPage indexPage, int ballotId) {
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();

        castVotePage = castVotePage.castPreferenceVote(ballotId, OFFICE_NAME, CANDIDATE_NAME_2);
        castVotePage = castVotePage.castPreferenceVote(ballotId, OFFICE_NAME, CANDIDATE_NAME_2);

        castVotePage = castVotePage.clickBackToIndexPage().clickCastVotesSecondTryLink();

        castVotePage = castVotePage.castPreferenceVote(ballotId, OFFICE_NAME, CANDIDATE_NAME_2);
        return castVotePage.clickBackToIndexPage();
    }

}
