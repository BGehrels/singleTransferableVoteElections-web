/*
 * Copyright © 2014 Martin Wilke, Benjamin Gehrels
 *
 * This file is part of The Single Transferable Vote Elections Library.
 *
 * The Single Transferable Vote Elections Web Interface is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * The Single Transferable Vote Elections Web Interface is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with The Single Transferable Vote
 * Elections Web Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.ballotCasting.VoteType;
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
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public final class MartinWilkesExampleIT {
    private static final String OFFICE_NAME = "Example Office";

    @Value("${local.server.port}")
    int port;
    private WebDriver driver;


    @Before
    public void setUp() throws MalformedURLException {
        driver = new HtmlUnitDriver();
        driver.navigate().to(new URL("http", "localhost", port, "/"));
    }

    @After
    public void tearDown() {
        driver.close();
    }

	@Test
	public void exampleByMartinWilke() {
        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);

        AdministrateBallotLayoutPage administrateBallotLayoutPage = indexPage.clickAdministrateBallotLayoutLink();
        administrateBallotLayoutPage = createElection(administrateBallotLayoutPage);
        indexPage = administrateBallotLayoutPage.clickBallotLayoutCompleted();

        indexPage = castVotes(indexPage.clickCastVotesFirstTryLink()).clickBackToIndexPage();
        indexPage = castVotes(indexPage.clickCastVotesSecondTryLink()).clickBackToIndexPage();

        ManageElectionCalculationsPage manageElectionCalculationsPage = indexPage.clickElectionCalculationLink();
        manageElectionCalculationsPage = manageElectionCalculationsPage.clickStartNewElectionCalculation(ManageElectionCalculationsPage.class);
        ElectionCalculationPage electionCalculationPage = manageElectionCalculationsPage.clickElectionCalculation();
        electionCalculationPage = electionCalculationPage.waitUntilAmbiguityResolutionIsNecessary();
        electionCalculationPage.chooseCandidate("G");
        electionCalculationPage.setDescription("Just choose the first one");
        electionCalculationPage.submitAmbiguityResolution().waitForElectionCalculationToBeFinished();

        assertThat(electionCalculationPage.getFemaleExclusiveElectedCandidateNames(OFFICE_NAME), is(empty()));
        assertThat(electionCalculationPage.getNonFemaleExclusiveElectedCandidateNames(OFFICE_NAME), containsInAnyOrder("C", "E", "F"));
	}

    private AdministrateBallotLayoutPage createElection(AdministrateBallotLayoutPage administrateBallotLayoutPage) {
        administrateBallotLayoutPage.setOfficeName(0, OFFICE_NAME);
        administrateBallotLayoutPage.setNumberOfFemaleExclusivePositions(0, 0);
        administrateBallotLayoutPage.setNumberOfNonFemaleExclusivePositions(0, 4);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "A", 0, true);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "B", 1, false);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "C", 2, true);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "D", 3, false);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "E", 4, true);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "F", 5, false);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "G", 6, true);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "H", 7, false);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "I", 8, true);
        administrateBallotLayoutPage = administrateBallotLayoutPage.clickAddCandidate(0);
        administrateBallotLayoutPage = createCandidate(administrateBallotLayoutPage, "J", 9, false);
        return administrateBallotLayoutPage;
    }

    private AdministrateBallotLayoutPage createCandidate(AdministrateBallotLayoutPage administrateBallotLayoutPage, String candidateName, int candidateIdx, boolean female) {
        administrateBallotLayoutPage.setCandidateName(0, candidateIdx, candidateName);
        administrateBallotLayoutPage.setCandidateFemale(0, candidateIdx, female);
        return administrateBallotLayoutPage;
    }

    private CastVotePage castVotes(CastVotePage castVotePage) {
        castVotePage = castVotePage.setBallotId(1).setPreferences(OFFICE_NAME, "A", "B", "D", "C").clickCastVote();
        castVotePage = castVotePage.setBallotId(2).setPreferences(OFFICE_NAME, "A","C","B","D","E").clickCastVote();
        castVotePage = castVotePage.setBallotId(3).setPreferences(OFFICE_NAME, "C").clickCastVote();
        castVotePage = castVotePage.setBallotId(4).setPreferences(OFFICE_NAME, "C","A","E").clickCastVote();
        castVotePage = castVotePage.setBallotId(5).setPreferences(OFFICE_NAME, "C","B","A","F","E","D","G").clickCastVote();
        castVotePage = castVotePage.setBallotId(6).setPreferences(OFFICE_NAME, "C","B","D","E").clickCastVote();
        castVotePage = castVotePage.setBallotId(7).setPreferences(OFFICE_NAME, "C", "F", "B", "D", "E", "H").clickCastVote();
        castVotePage = castVotePage.setBallotId(8).setPreferences(OFFICE_NAME, "C", "D", "F", "E", "H", "A").clickCastVote();
        castVotePage = castVotePage.setBallotId(9).setPreferences(OFFICE_NAME, "D", "E", "C").clickCastVote();
        castVotePage = castVotePage.setBallotId(10).setPreferences(OFFICE_NAME, "E", "B", "D", "C", "A", "F").clickCastVote();
        castVotePage = castVotePage.setBallotId(11).setPreferences(OFFICE_NAME, "E", "D", "C", "A").clickCastVote();
        castVotePage = castVotePage.setBallotId(12).setPreferences(OFFICE_NAME, "F").clickCastVote();
        castVotePage = castVotePage.setBallotId(13).setPreferences(OFFICE_NAME, "F", "C", "H").clickCastVote();
        castVotePage = castVotePage.setBallotId(14).setPreferences(OFFICE_NAME, "F", "G", "E", "I", "H", "J").clickCastVote();
        castVotePage = castVotePage.setBallotId(15).setPreferences(OFFICE_NAME, "F", "H", "G").clickCastVote();
        castVotePage = castVotePage.setBallotId(16).setPreferences(OFFICE_NAME, "G", "F", "E", "I").clickCastVote();
        castVotePage = castVotePage.setBallotId(17).setPreferences(OFFICE_NAME, "H", "F", "J", "A", "I").clickCastVote();
        castVotePage = castVotePage.setBallotId(18).setPreferences(OFFICE_NAME, "H", "G", "I", "F").clickCastVote();
        castVotePage = castVotePage.setBallotId(19).setPreferences(OFFICE_NAME, "I", "J", "F").clickCastVote();
        castVotePage = castVotePage.setBallotId(20).setPreferences(OFFICE_NAME, "I", "J", "H").clickCastVote();
        castVotePage = castVotePage.setBallotId(21).setPreferences(OFFICE_NAME, "J", "I", "H", "F", "E").clickCastVote();
        castVotePage = castVotePage.setBallotId(22).setVoteType(OFFICE_NAME, VoteType.NO).clickCastVote();
        castVotePage = castVotePage.setBallotId(23).setVoteType(OFFICE_NAME, VoteType.INVALID).clickCastVote();
        castVotePage = castVotePage.setBallotId(24).setVoteType(OFFICE_NAME, VoteType.NOT_VOTED).clickCastVote();

        return castVotePage;
    }


}
