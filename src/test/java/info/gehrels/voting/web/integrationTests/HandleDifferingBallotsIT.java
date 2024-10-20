package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.ballotCasting.VoteType;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.CreateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.DifferingBallotsPage;
import info.gehrels.voting.web.integrationTests.pages.ElectionCalculationPage;
import info.gehrels.voting.web.integrationTests.pages.IndexPage;
import info.gehrels.voting.web.integrationTests.pages.ManageElectionCalculationsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringConfig.class, webEnvironment = RANDOM_PORT)
public final class HandleDifferingBallotsIT {
    public static final String CANDIDATE_NAME_1 = "The first candidate";
    public static final String CANDIDATE_NAME_2 = "The second candidate";
    public static final String OFFICE_NAME = "The Office";

    @Value("${local.server.port}")
    int port;

    public WebDriver driver;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        driver = new HtmlUnitDriver();
        driver.navigate().to(new URL("http", "localhost", port, "/"));
    }

    @Test
    public void handleDifferingBallots() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);
        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();

        createBallotLayoutPage.setOfficeName(0, OFFICE_NAME);
        createBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 0);
        createBallotLayoutPage.setNumberOfNotFemaleExclusivePositions(0, 1);

        createBallotLayoutPage.setCandidateName(0, 0, CANDIDATE_NAME_1);
        createBallotLayoutPage.setCandidateFemale(0, 0, true);
        createBallotLayoutPage = createBallotLayoutPage.clickAddCandidate(0);

        createBallotLayoutPage.setCandidateName(0, 1, CANDIDATE_NAME_2);
        createBallotLayoutPage.setCandidateFemale(0, 1, false);

        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();
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
        assertThat(electionCalculationPage.getNotFemaleExclusiveElectedCandidateNames(OFFICE_NAME), contains(CANDIDATE_NAME_1));
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
