package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.lift.find.PageTitleFinder;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public class IndexPage {
    public static final String ADMINISTRATE_BALLOT_LAYOUT_LINK = "Stimmzettellayout erstellen/bearbeiten (löscht eventuell bereits eingegebene Stimmen)";

    private final WebDriver webDriver;

    @FindBy(linkText = ADMINISTRATE_BALLOT_LAYOUT_LINK)
    private WebElement administrateBallotLayoutLink;

    @FindBy(linkText = "Stimmen eingeben (Ersteingabe)")
    private WebElement castVotesFirstTryLink;

    @FindBy(linkText = "Stimmen eingeben (Kontrolleingabe)")
    private WebElement castVotesSecondTryLink;

    @FindBy(linkText = "Ergebnisberechnung")
    private WebElement electionCalculationLink;

    public IndexPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        new WebDriverWait(webDriver, 60).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return input.findElement(By.linkText(ADMINISTRATE_BALLOT_LAYOUT_LINK)).isDisplayed();
            }
        });
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
