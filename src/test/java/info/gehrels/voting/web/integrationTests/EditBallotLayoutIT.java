package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.integrationTests.pages.CastVotePage;
import info.gehrels.voting.web.integrationTests.pages.CreateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.EditBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.IndexPage;
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

import static info.gehrels.voting.web.integrationTests.HandleDifferingBallotsIT.OFFICE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringConfig.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditBallotLayoutIT {
    private static final String CANDIDATE_NAME_1 = "The Candidate";
    private static final String CANDIDATE_NAME_2 = "The second Candidate";
    private static final String OFFICE_NAME_1 = "The Office";
    private static final String OFFICE_NAME_2 = "The second office";

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
    public void twoElectionsDifferentCandidatesWalkThrough() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        CreateBallotLayoutPage createBallotLayoutPage = indexPage.clickCreateBallotLayoutLink();
        createBallotLayoutPage = createFirstElection(createBallotLayoutPage);

        indexPage = createBallotLayoutPage.clickBallotLayoutCompleted();;

        EditBallotLayoutPage editBallotLayoutPage = indexPage.clickEditBallotLayoutLink();
        editBallotLayoutPage.setNewOfficeName(0, OFFICE_NAME_2);
        editBallotLayoutPage.clickRenameOffice(0);
        IndexPage indexPage1 = editBallotLayoutPage.clickBackToIndexPage();
        CastVotePage castVotePage = indexPage1.clickCastVotesFirstTryLink();
        assertThat(castVotePage.hasOfficeWithName(OFFICE_NAME), is(false));
        assertThat(castVotePage.hasOfficeWithName(OFFICE_NAME_2), is(true));
    }

    private CreateBallotLayoutPage createFirstElection(CreateBallotLayoutPage createBallotLayoutPage) {
        createBallotLayoutPage.setOfficeName(0, OFFICE_NAME_1);
        createBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 1);
        createBallotLayoutPage.setNumberOfNonFemaleExclusivePositions(0, 1);
        createBallotLayoutPage.setCandidateName(0, 0, CANDIDATE_NAME_1);
        createBallotLayoutPage.setCandidateFemale(0, 0, true);
        createBallotLayoutPage = createBallotLayoutPage.clickAddCandidate(0);
        createBallotLayoutPage.setCandidateName(0, 1, CANDIDATE_NAME_2);
        createBallotLayoutPage.setCandidateFemale(0, 1, false);
        return createBallotLayoutPage;
    }
}
