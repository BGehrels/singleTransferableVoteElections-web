package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.CreateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.EditBallotLayoutPage;
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
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditBallotLayoutIT {
    private static final String FEMALE_CANDIDATES_NAME = "The Candidate";
    private static final String NON_FEMALE_CANDIDATES_NAME = "The second Candidate";
    private static final String ORIGINAL_OFFICE_NAME = "The Office";
    private static final String NEW_OFFICE_NAME = "The second office";

    @Value("${local.server.port}")
    int port;

    public WebDriver driver;

    @BeforeEach
    public void setUp() throws MalformedURLException {
        driver = new HtmlUnitDriver();
        driver.navigate().to(new URL("http", "localhost", port, "/"));
    }

    @Test
    public void changeOfficeName() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        // Given we created a ballot
        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage = createElection(createBallotLayoutPage, 2, 4);
        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();

        // And Given there have already been votes cast
        indexPage = castABallot(indexPage, 1);
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
        assertThat(electionCalculationPage.getNotFemaleExclusiveElectedCandidateNames(NEW_OFFICE_NAME), contains(NON_FEMALE_CANDIDATES_NAME));
    }

    @Test
    public void changeNumberOfFemaleExclusivePositions() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        // Given we created a ballot
        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage = createElection(createBallotLayoutPage, 4, 4);
        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();

        // And Given there have already been votes cast
        indexPage = castABallot(indexPage, 1);
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
        assertThat(electionCalculationPage.getNotFemaleExclusiveElectedCandidateNames(ORIGINAL_OFFICE_NAME), contains(NON_FEMALE_CANDIDATES_NAME));
    }

    @Test
    public void changeNumberOfNotFemaleExclusivePositions() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        // Given we created a ballot
        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage = createElection(createBallotLayoutPage, 1, 4);
        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();

        // And Given there have already been votes cast
        indexPage = castABallot(indexPage, 1);
        assertThat(indexPage.getNumberOfCastVotesFirstTry(), is("1"));
        assertThat(indexPage.getNumberOfCastVotesSecondTry(), is("1"));

        // When we edit the number of not female exclusive positions of the office
        EditBallotLayoutPage editBallotLayoutPage = indexPage.clickEditBallotLayoutLink();
        editBallotLayoutPage.setNewNumberOfNotFemaleExclusivePositions(ORIGINAL_OFFICE_NAME, 1);
        indexPage = editBallotLayoutPage.clickChangeNumberOfNotFemaleExclusivePositions(ORIGINAL_OFFICE_NAME).clickBackToIndexPage();

        // then the cast votes still exist
        assertThat(indexPage.getNumberOfCastVotesFirstTry(), is("1"));
        assertThat(indexPage.getNumberOfCastVotesSecondTry(), is("1"));

        // And they are correctly included in the election calculation under the new office name
        ElectionCalculationPage electionCalculationPage = indexPage.clickElectionCalculationLink()
                .clickStartNewElectionCalculation(ManageElectionCalculationsPage.class)
                .clickElectionCalculation();
        assertThat(electionCalculationPage.getNumberOfFemaleExclusivePositions(ORIGINAL_OFFICE_NAME), is(1L));
        assertThat(electionCalculationPage.getNumberOfNotFemaleExclusivePositions(ORIGINAL_OFFICE_NAME), is(1L));
        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(ORIGINAL_OFFICE_NAME), contains(FEMALE_CANDIDATES_NAME));
        assertThat(electionCalculationPage.getNotFemaleExclusiveElectedCandidateNames(ORIGINAL_OFFICE_NAME), contains(NON_FEMALE_CANDIDATES_NAME));
    }

    @Test
    public void changeCandidatesGender() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        // Given we created a ballot
        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage = createElection(createBallotLayoutPage, 5, 0);
        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();

        // And Given there have already been votes cast
        indexPage = castABallot(indexPage, 1);
        indexPage = castABallot(indexPage, 2);
        indexPage = castABallot(indexPage, 3);
        indexPage = castABallot(indexPage, 4);
        assertThat(indexPage.getNumberOfCastVotesFirstTry(), is("4"));
        assertThat(indexPage.getNumberOfCastVotesSecondTry(), is("4"));

        // When we change the female candidate to candidate on an open position
        EditBallotLayoutPage editBallotLayoutPage = indexPage.clickEditBallotLayoutLink();
        indexPage = editBallotLayoutPage.clickSwitchIsFemale(ORIGINAL_OFFICE_NAME, NON_FEMALE_CANDIDATES_NAME).clickBackToIndexPage();

        // then the cast votes still exist
        assertThat(indexPage.getNumberOfCastVotesFirstTry(), is("4"));
        assertThat(indexPage.getNumberOfCastVotesSecondTry(), is("4"));

        // And they are correctly included in the election calculation under the new office name
        ElectionCalculationPage electionCalculationPage = indexPage.clickElectionCalculationLink()
                .clickStartNewElectionCalculation(ManageElectionCalculationsPage.class)
                .clickElectionCalculation();
        assertThat(electionCalculationPage.getNumberOfFemaleExclusivePositions(ORIGINAL_OFFICE_NAME), is(5L));
        assertThat(electionCalculationPage.getNumberOfNotFemaleExclusivePositions(ORIGINAL_OFFICE_NAME), is(0L));
        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(ORIGINAL_OFFICE_NAME), containsInAnyOrder(FEMALE_CANDIDATES_NAME, NON_FEMALE_CANDIDATES_NAME));
        assertThat(electionCalculationPage.getNotFemaleExclusiveElectedCandidateNames(ORIGINAL_OFFICE_NAME), is(empty()));
    }

    private IndexPage castABallot(IndexPage indexPage, int id) {
        indexPage = castAPreferenceVote(indexPage.clickCastVotesFirstTryLink(), id).clickBackToIndexPage();
        indexPage = castAPreferenceVote(indexPage.clickCastVotesSecondTryLink(), id).clickBackToIndexPage();
        return indexPage;
    }

    private CreateBallotLayoutPage createElection(CreateBallotLayoutPage createBallotLayoutPage, int numberOfFemaleExclusivePositions, int numberOfNotFemaleExclusivePositions) {
        createBallotLayoutPage.setOfficeName(0, ORIGINAL_OFFICE_NAME);
        createBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, numberOfFemaleExclusivePositions);
        createBallotLayoutPage.setNumberOfNotFemaleExclusivePositions(0, numberOfNotFemaleExclusivePositions);
        createBallotLayoutPage.setCandidateName(0, 0, FEMALE_CANDIDATES_NAME);
        createBallotLayoutPage.setCandidateFemale(0, 0, true);
        createBallotLayoutPage = createBallotLayoutPage.clickAddCandidate(0);
        createBallotLayoutPage.setCandidateName(0, 1, NON_FEMALE_CANDIDATES_NAME);
        createBallotLayoutPage.setCandidateFemale(0, 1, false);
        return createBallotLayoutPage;
    }

    private CastVotePage castAPreferenceVote(CastVotePage castVotePage, int id) {
        return castVotePage.setBallotId(id).setPreferences(ORIGINAL_OFFICE_NAME, FEMALE_CANDIDATES_NAME, NON_FEMALE_CANDIDATES_NAME).clickCastVote();
    }

}
