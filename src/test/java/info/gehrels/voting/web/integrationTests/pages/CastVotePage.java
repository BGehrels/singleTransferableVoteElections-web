package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.base.Predicate;
import info.gehrels.voting.web.VoteType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public final class CastVotePage {
    private final WebDriver webDriver;

    @FindBy(name="ballotId")
    private WebElement ballotIdField;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement submitButton;

    @FindBy(linkText = "Zurück zur Startseite")
    private WebElement backToIndexPageLink;

    public CastVotePage(final WebDriver webDriver) {
        this.webDriver = webDriver;
        new WebDriverWait(webDriver, 60).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                WebElement element = webDriver.findElement(By.name("ballotId"));
                return element.isDisplayed() && element.getText().isEmpty();
            }
        });
    }

    public void setBallotId(int id) {
        WebElement ballotIdField = webDriver.findElement(By.name("ballotId"));
        ballotIdField.clear();
        ballotIdField.sendKeys(Integer.toString(id));
    }

    public void setVoteType(String officeName, VoteType voteType) {
        List<WebElement> elements = findOfficeSection(officeName).findElements(By.xpath(".//input[@type='radio']"));
        WebElementUtils.findByValue(elements, voteType.toString()).click();
    }

    public void setPreference(String officeName, String candidateName, int preference) {
        WebElement officeSection = findOfficeSection(officeName);
        WebElement candidatePreference =
                findCandidateListItem(officeSection, candidateName).findElement(By.tagName("input"));
        candidatePreference.clear();
        candidatePreference.sendKeys(Integer.toString(preference));
    }

    private WebElement findCandidateListItem(WebElement officeSection, String candidateName) {
        return officeSection.findElement(By.xpath(".//li/span[contains(text(), '" + candidateName + "')]/.."));
    }

    public CastVotePage clickCastVote() {
        submitButton.click();
        return PageFactory.initElements(webDriver, CastVotePage.class);
    }

    public IndexPage clickBackToIndexPage() {
         backToIndexPageLink.click();
        return PageFactory.initElements(webDriver, IndexPage.class);

    }

    private WebElement findOfficeSection(String officeName) {
        return webDriver.findElement(By.xpath("//section[@class='election']/h2[contains(text(), '" + officeName + "')]/.."));
    }
}
