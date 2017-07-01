package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.base.Predicate;
import info.gehrels.voting.web.ballotCasting.VoteType;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static info.gehrels.voting.web.integrationTests.pages.WebElementUtils.setInputText;

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

    public CastVotePage castPreferenceVote(int ballotId, String officeName, String... candidateNamesOrNull) {
        setBallotId(ballotId);
        setPreferences(officeName, candidateNamesOrNull);
        return clickCastVote();
    }

    public CastVotePage setPreferences(String officeName, String... candidateNamesOrNull) {
        for (int i = 0; i < candidateNamesOrNull.length; i++) {
            String candidateNameOrNull = candidateNamesOrNull[i];
            if (candidateNameOrNull != null) {
                setPreference(officeName, candidateNameOrNull, i + 1);
            }
        }

        return this;
    }

    public CastVotePage castNonPreferenceVote(int ballotId, String officeName, VoteType voteType) {
        setBallotId(ballotId);
        setVoteType(officeName, voteType);
        return clickCastVote();
    }

    public CastVotePage setBallotId(int id) {
        setInputText(webDriver.findElement(By.name("ballotId")), Integer.toString(id));
        return this;
    }

    public CastVotePage setVoteType(String officeName, VoteType voteType) {
        List<WebElement> elements = findOfficeSection(officeName).findElements(By.xpath(".//input[@type='radio']"));
        WebElementUtils.findByValue(elements, voteType.toString()).click();
        return this;
    }

    public void setPreference(String officeName, String candidateName, int preference) {
        WebElement officeSection = findOfficeSection(officeName);
        WebElement candidatePreference =
                findCandidateListItem(officeSection, candidateName).findElement(By.tagName("input"));
        setInputText(candidatePreference, Integer.toString(preference));
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

    public boolean hasOfficeWithName(String officeName) {
        try {
            findOfficeSection(officeName);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private WebElement findOfficeSection(String officeName) {
        return webDriver.findElement(By.xpath("//section[@class='election']/h2[contains(text(), '" + officeName + "')]/.."));
    }
}
