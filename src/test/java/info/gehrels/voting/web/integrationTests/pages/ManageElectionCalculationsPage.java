package info.gehrels.voting.web.integrationTests.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ManageElectionCalculationsPage {

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement startNewElectionCalculation;

    @FindBy(xpath = "//li/a")
    private WebElement firstElectionCalculation;

    private final WebDriver webDriver;

    public ManageElectionCalculationsPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public ManageElectionCalculationsPage clickStartNewElectionCalculation() {
        startNewElectionCalculation.click();
        return PageFactory.initElements(webDriver, ManageElectionCalculationsPage.class);
    }

    public ElectionCalculationPage clickElectionCalculation() {
        firstElectionCalculation.click();
        return PageFactory.initElements(webDriver, ElectionCalculationPage.class);
    }
}
