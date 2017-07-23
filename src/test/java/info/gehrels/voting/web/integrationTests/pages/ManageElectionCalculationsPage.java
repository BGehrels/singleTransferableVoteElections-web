package info.gehrels.voting.web.integrationTests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import static org.junit.Assert.fail;

public final class ManageElectionCalculationsPage {
    public static final String INPUT_TYPE_SUBMIT = "//input[@type='submit']";

    @FindBy(xpath = INPUT_TYPE_SUBMIT)
    private WebElement startNewElectionCalculation;

    @FindBy(xpath = "//li/a[1]")
    private WebElement firstElectionCalculation;

    private final WebDriver webDriver;

    public ManageElectionCalculationsPage(final WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public <T> T clickStartNewElectionCalculation(Class<T> expectedResult) {
        startNewElectionCalculation.click();
        return PageFactory.initElements(webDriver, expectedResult);
    }

    public ElectionCalculationPage clickElectionCalculation() {
        firstElectionCalculation.click();
        while(electionCalculationStillInProgress()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Test interrupted");
            }
            webDriver.navigate().refresh();
        }
        return PageFactory.initElements(webDriver, ElectionCalculationPage.class);
    }

    private boolean electionCalculationStillInProgress() {
        return
                webDriver.getPageSource().contains("Die Wahlergebnisse werden momentan berechnet") ||
                webDriver.getPageSource().contains("Die Ergebnisberechnung wurde noch nicht gestartet.");
    }
}
