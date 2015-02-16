package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class ManageElectionCalculationsPage {

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement startNewElectionCalculation;

    @FindBy(xpath = "//li/a")
    private WebElement firstElectionCalculation;

    private final WebDriver webDriver;

    public ManageElectionCalculationsPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public <T> T clickStartNewElectionCalculation(Class<T> expectedResult) {
        startNewElectionCalculation.click();
        return PageFactory.initElements(webDriver, expectedResult);
    }

    public ElectionCalculationPage clickElectionCalculation() {
        firstElectionCalculation.click();
        return PageFactory.initElements(webDriver, ElectionCalculationPage.class);
    }
}
