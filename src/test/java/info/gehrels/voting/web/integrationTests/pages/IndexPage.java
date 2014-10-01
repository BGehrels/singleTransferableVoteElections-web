package info.gehrels.voting.web.integrationTests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.lift.find.PageTitleFinder;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class IndexPage {
    private WebDriver webDriver;

    @FindBy(linkText = "Stimmzettellayout erstellen/bearbeiten (löscht eventuell bereits eingegebene Stimmen)")
    private WebElement administrateBallotLayoutLink;

    @FindBy(linkText = "Stimmen eingeben (Ersteingabe)")
    private WebElement castVotesFirstTryLink;

    @FindBy(linkText = "Stimmen eingeben (Kontrolleingabe)")
    private WebElement castVotesSecondTryLink;

    @FindBy(linkText = "Ergebnisberechnung")
    private WebElement electionCalculationLink;

    public IndexPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public AdministrateBallotLayoutPage clickAdministrateBallotLayoutLink() {
        administrateBallotLayoutLink.click();
        return PageFactory.initElements(webDriver, AdministrateBallotLayoutPage.class);
    }

    public CastVotePage clickCastVotesFirstTryLink() {
        castVotesFirstTryLink.click();
        return PageFactory.initElements(webDriver, CastVotePage.class);
    }

    public CastVotePage clickCastVotesSecondTryLink() {
        castVotesSecondTryLink.click();
        return PageFactory.initElements(webDriver, CastVotePage.class);
    }


    public ManageElectionCalculationsPage clickElectionCalculationLink() {
        electionCalculationLink.click();
        return PageFactory.initElements(webDriver, ManageElectionCalculationsPage.class);
    }
}
