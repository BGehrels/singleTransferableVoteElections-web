package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static info.gehrels.voting.web.integrationTests.pages.WebElementUtils.setInputText;

public final class AdministrateBallotLayoutPage {
    private final WebDriver webDriver;


    @FindBy(xpath = "//input[@type='submit']")
    private WebElement ballotLayoutCompletedButton;

    public AdministrateBallotLayoutPage(final WebDriver webDriver) {
        this.webDriver = webDriver;

        new WebDriverWait(webDriver, 60).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return webDriver.findElement(By.linkText("Zurück zur Startseite")).isDisplayed();
            }
        });
    }

    public void setOfficeName(int officeIndex, String officeName) {
        setInputText(webDriver.findElement(By.name("elections[" + officeIndex + "].officeName")), officeName);
    }

    public void setNumberOfFemaleExclusivePositions(int officeIndex, int value) {
        setInputText(webDriver.findElement(By.name("elections[" + officeIndex + "].numberOfFemaleExclusivePositions")), Integer.toString(value));
    }

    public void setNumberOfNonFemaleExclusivePositions(int officeIndex, int value) {
        setInputText(webDriver.findElement(By.name("elections[" + officeIndex + "].numberOfNonFemaleExclusivePositions")), Integer.toString(value));
    }

    public void setCandidateName(int officeIndex, int candidateIdx, String candidateName) {
        setInputText(webDriver.findElement(By.name("elections[" + officeIndex + "].candidates[" + candidateIdx + "].name")), candidateName);
    }

    public void setCandidateFemale(int officeIndex, int candidateIdx, boolean female) {
        List<WebElement> radioButtons = webDriver.findElements(By.name("elections[" + officeIndex + "].candidates[" + candidateIdx + "]" + ".female"));
        if (female) {
            WebElementUtils.findByValue(radioButtons, Boolean.toString(true)).click();
        } else {
            WebElementUtils.findByValue(radioButtons, Boolean.toString(false)).click();
        }
    }

    public IndexPage clickBallotLayoutCompleted() {
        ballotLayoutCompletedButton.click();
        return PageFactory.initElements(webDriver, IndexPage.class);
    }

    public AdministrateBallotLayoutPage clickAddCandidate(int officeIndex) {
        webDriver.findElement(By.xpath("//*[@name='addNewCandidate' and @value='" + officeIndex + "']")).click();
        return PageFactory.initElements(webDriver, AdministrateBallotLayoutPage.class);
    }
}
