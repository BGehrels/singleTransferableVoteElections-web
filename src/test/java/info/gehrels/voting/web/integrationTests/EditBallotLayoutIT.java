package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.CreateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.EditBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.ElectionCalculationPage;
import info.gehrels.voting.web.integrationTests.pages.IndexPage;
import info.gehrels.voting.web.integrationTests.pages.ManageElectionCalculationsPage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditBallotLayoutIT {
    private static final String FEMALE_CANDIDATES_NAME = "The Candidate";
    private static final String NON_FEMALE_CANDIDATES_NAME = "The second Candidate";
    private static final String ORIGINAL_OFFICE_NAME = "The Office";
    private static final String NEW_OFFICE_NAME = "The second office";

    @Value("${local.server.port}")
    int port;
    private WebDriver driver;

    @Before
    public void setUp() throws MalformedURLException {
        driver = new ChromeDriver();
        driver.navigate().to(new URL("http", "localhost", port, "/"));
    }

    @After
    public void tearDown() {
        driver.close();
    }

    @Test
    public void changeOfficeName() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        // Given we created a ballot
        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage = createFirstElection(createBallotLayoutPage);
        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();

        // And Given there have already been votes cast
        indexPage = castAPreferenceVote(indexPage.clickCastVotesFirstTryLink()).clickBackToIndexPage();
        indexPage = castAPreferenceVote(indexPage.clickCastVotesSecondTryLink()).clickBackToIndexPage();
        assertThat(indexPage.getNumberOfCastVotesFirstTry(), is("1"));
        assertThat(indexPage.getNumberOfCastVotesSecondTry(), is("1"));

        // When we edit an office name
        EditBallotLayoutPage editBallotLayoutPage = indexPage.clickEditBallotLayoutLink();
        editBallotLayoutPage.setNewOfficeName(ORIGINAL_OFFICE_NAME, NEW_OFFICE_NAME);
        editBallotLayoutPage.clickRenameOffice(ORIGINAL_OFFICE_NAME);
        indexPage = editBallotLayoutPage.clickBackToIndexPage();

        // Then the office Name is changed on the cast Votes page
        CastVotePage castVotePage = indexPage.clickCastVotesFirstTryLink();
        assertThat(castVotePage.hasOfficeWithName(ORIGINAL_OFFICE_NAME), is(false));
        assertThat(castVotePage.hasOfficeWithName(NEW_OFFICE_NAME), is(true));

        // and the cast votes still exist
        IndexPage indexPage2 = castVotePage.clickBackToIndexPage();
        assertThat(indexPage2.getNumberOfCastVotesFirstTry(), is("1"));
        assertThat(indexPage2.getNumberOfCastVotesSecondTry(), is("1"));

        // And they are correctly included in the election calculation under the new office name
        ElectionCalculationPage electionCalculationPage = indexPage.clickElectionCalculationLink()
                .clickStartNewElectionCalculation(ManageElectionCalculationsPage.class)
                .clickElectionCalculation();
        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(NEW_OFFICE_NAME), contains(FEMALE_CANDIDATES_NAME));
        assertThat(electionCalculationPage.getNonFemaleExclusiveElectedCandidateNames(NEW_OFFICE_NAME), contains(NON_FEMALE_CANDIDATES_NAME));
    }

    @Test
    public void changeOfFemaleExclusivePositions() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        // Given we created a ballot
        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage = createFirstElection(createBallotLayoutPage);
        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();

        // And Given there have already been votes cast
        indexPage = castAPreferenceVote(indexPage.clickCastVotesFirstTryLink()).clickBackToIndexPage();
        indexPage = castAPreferenceVote(indexPage.clickCastVotesSecondTryLink()).clickBackToIndexPage();
        assertThat(indexPage.getNumberOfCastVotesFirstTry(), is("1"));
        assertThat(indexPage.getNumberOfCastVotesSecondTry(), is("1"));

        // When we edit the number of female exclusive positions of the office
        EditBallotLayoutPage editBallotLayoutPage = indexPage.clickEditBallotLayoutLink();
        editBallotLayoutPage.setNewNumberOfFemaleExclusivePositions(ORIGINAL_OFFICE_NAME, 1);
        indexPage = editBallotLayoutPage.clickChangeNumberOfFemaleExclusivePositions(ORIGINAL_OFFICE_NAME).clickBackToIndexPage();

        // then the cast votes still exist
        assertThat(indexPage.getNumberOfCastVotesFirstTry(), is("1"));
        assertThat(indexPage.getNumberOfCastVotesSecondTry(), is("1"));

        // And they are correctly included in the election calculation under the new office name
        ElectionCalculationPage electionCalculationPage = indexPage.clickElectionCalculationLink()
                .clickStartNewElectionCalculation(ManageElectionCalculationsPage.class)
                .clickElectionCalculation();
        assertThat(electionCalculationPage.getNumberOfFemaleExclusivePositions(ORIGINAL_OFFICE_NAME), is(1L));
        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(ORIGINAL_OFFICE_NAME), contains(FEMALE_CANDIDATES_NAME));
        assertThat(electionCalculationPage.getNonFemaleExclusiveElectedCandidateNames(ORIGINAL_OFFICE_NAME), contains(NON_FEMALE_CANDIDATES_NAME));
    }

    private CreateBallotLayoutPage createFirstElection(CreateBallotLayoutPage createBallotLayoutPage) {
        createBallotLayoutPage.setOfficeName(0, ORIGINAL_OFFICE_NAME);
        createBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 4);
        createBallotLayoutPage.setNumberOfNonFemaleExclusivePositions(0, 4);
        createBallotLayoutPage.setCandidateName(0, 0, FEMALE_CANDIDATES_NAME);
        createBallotLayoutPage.setCandidateFemale(0, 0, true);
        createBallotLayoutPage = createBallotLayoutPage.clickAddCandidate(0);
        createBallotLayoutPage.setCandidateName(0, 1, NON_FEMALE_CANDIDATES_NAME);
        createBallotLayoutPage.setCandidateFemale(0, 1, false);
        return createBallotLayoutPage;
    }

    private CastVotePage castAPreferenceVote(CastVotePage castVotePage) {
        return castVotePage.setBallotId(1).setPreferences(ORIGINAL_OFFICE_NAME, FEMALE_CANDIDATES_NAME, NON_FEMALE_CANDIDATES_NAME).clickCastVote();
    }
}
