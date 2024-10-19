package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.ballotCasting.VoteType;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.CreateBallotLayoutPage;
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
import static org.hamcrest.Matchers.containsString;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringConfig.class, webEnvironment = RANDOM_PORT)
public final class AmbiguityResolutionIT {
    private static final String CANDIDATE_NAME_1 = "The first candidate";
    private static final String CANDIDATE_NAME_2 = "The second candidate";
    private static final String OFFICE_NAME = "The Office";
    private static final String AMBIGUITY_RESOLUTION_DESCRIPTION = "Ambiguity resolution description";

    @Value("${local.server.port}")
    private int port;

    public WebDriver driver = new HtmlUnitDriver();

    @BeforeEach
    public void setUp() throws MalformedURLException {
        driver = new HtmlUnitDriver();
        driver.navigate().to(new URL("http", "localhost", port, "/"));
    }

    @Test
    public void createAmbiguousSituationAndResolveIt() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage.setOfficeName(0, OFFICE_NAME);
        createBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 0);
        createBallotLayoutPage.setNumberOfNotFemaleExclusivePositions(0, 1);
        createBallotLayoutPage.setCandidateName(0, 0, CANDIDATE_NAME_1);
        createBallotLayoutPage.setCandidateFemale(0, 0, false);
        createBallotLayoutPage.clickAddCandidate(0);
        createBallotLayoutPage.setCandidateName(0, 1, CANDIDATE_NAME_2);
        createBallotLayoutPage.setCandidateFemale(0, 1, false);
        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();

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

        electionCalculationPage = electionCalculationPage.waitUntilAmbiguityResolutionIsNecessary();
        electionCalculationPage.chooseCandidate(CANDIDATE_NAME_2);
        electionCalculationPage.setDescription(AMBIGUITY_RESOLUTION_DESCRIPTION);
        electionCalculationPage = electionCalculationPage.submitAmbiguityResolution();

        electionCalculationPage.waitForElectionCalculationToBeFinished();
        assertThat(electionCalculationPage.getWinningNotFemaleExclusiveCandidate(OFFICE_NAME), contains(CANDIDATE_NAME_1));
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
