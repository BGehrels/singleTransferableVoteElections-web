package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class ManageElectionCalculationsPage {
    public static final String INPUT_TYPE_SUBMIT = "//input[@type='submit']";

    @FindBy(xpath = INPUT_TYPE_SUBMIT)
    private WebElement startNewElectionCalculation;

    @FindBy(xpath = "//li/a")
    private WebElement firstElectionCalculation;

    private final WebDriver webDriver;

    public ManageElectionCalculationsPage(final WebDriver webDriver) {
        this.webDriver = webDriver;
        new WebDriverWait(webDriver, 60).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return webDriver.findElement(By.xpath(INPUT_TYPE_SUBMIT)).isDisplayed();
            }
                                               }
        );
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
