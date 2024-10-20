package info.gehrels.voting.web.integrationTests.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class IndexPage {
    public static final String CREATE_BALLOT_LAYOUT_LINK = "Stimmzettellayout erstellen/überschreiben (löscht eventuell bereits eingegebene Stimmen)";

    private final WebDriver webDriver;

    @FindBy(linkText = CREATE_BALLOT_LAYOUT_LINK)
    private WebElement createBallotLayoutLink;

    @FindBy(linkText = "Stimmzettellayout bearbeiten (bereits eingegebene Stimmen bleiben erhalten)")
    private WebElement editBallotLayoutLink;

    @FindBy(linkText = "Stimmen eingeben (Ersteingabe)")
    private WebElement castVotesFirstTryLink;

    @FindBy(linkText = "Stimmen eingeben (Kontrolleingabe)")
    private WebElement castVotesSecondTryLink;

    @FindBy(linkText = "Ergebnisberechnung")
    private WebElement electionCalculationLink;

    @FindBy(xpath = "//dt[text()='Ersteingabe']/following-sibling::dd")
    private WebElement numberOfCastVotesFirstTry;

    @FindBy(xpath = "//dt[text()='Kontrolleingabe']/following-sibling::dd")
    private WebElement numberOfCastVotesSecondTry;

    public IndexPage(WebDriver webDriver) {
        this.webDriver = webDriver;
        new WebDriverWait(webDriver, Duration.of(60, SECONDS))
                .until(input -> input.findElement(By.linkText(CREATE_BALLOT_LAYOUT_LINK)).isDisplayed());
    }

    public CreateBallotLayoutPage clickCreateBallotLayoutLink() {
        createBallotLayoutLink.click();
        return PageFactory.initElements(webDriver, CreateBallotLayoutPage.class);
    }

    public EditBallotLayoutPage clickEditBallotLayoutLink() {
        editBallotLayoutLink.click();
        return PageFactory.initElements(webDriver, EditBallotLayoutPage.class);
    }

    public CastVotePage clickCastVotesFirstTryLink() {
        castVotesFirstTryLink.click();
        return PageFactory.initElements(webDriver, CastVotePage.class);
    }

    public CastVotePage clickCastVotesSecondTryLink() {
        castVotesSecondTryLink.click();
        return PageFactory.initElements(webDriver, CastVotePage.class);
    }

    public String getNumberOfCastVotesFirstTry() {
        return numberOfCastVotesFirstTry.getText();
    }

    public String getNumberOfCastVotesSecondTry() {
        return numberOfCastVotesSecondTry.getText();
    }


    public ManageElectionCalculationsPage clickElectionCalculationLink() {
        electionCalculationLink.click();
        return PageFactory.initElements(webDriver, ManageElectionCalculationsPage.class);
    }
}
