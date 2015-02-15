package info.gehrels.voting.web.integrationTests.pages;

import info.gehrels.voting.web.VoteType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public final class CastVotePage {
    private final WebDriver webDriver;

    @FindBy(name="ballotId")
    private WebElement ballotIdField;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement submitButton;

    @FindBy(linkText = "Zurück zur Startseite")
    private WebElement backToIndexPageLink;

    public CastVotePage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void setBallotId(int id) {
        WebElement ballotIdField = webDriver.findElement(By.name("ballotId"));
        ballotIdField.clear();
        ballotIdField.sendKeys(Integer.toString(id));
    }

    public void setVoteType(int officeIdx, VoteType voteType) {
        List<WebElement> elements = webDriver.findElements(By.name("votesByElectionId[" + officeIdx + "].type"));
        WebElementUtils.findByValue(elements, voteType.toString()).click();
    }

    public void setPreference(int officeIdx, int candidateIdx, int preference) {
        WebElement element = webDriver.findElement(By.name("votesByElectionId[" + officeIdx + "].preferencesByCandidateIdx[" + candidateIdx + "].value"));
        element.clear();
        element.sendKeys(Integer.toString(preference));
    }

    public CastVotePage clickCastVote() {
        submitButton.click();
        return PageFactory.initElements(webDriver, CastVotePage.class);
    }

    public IndexPage clickBackToIndePage() {
         backToIndexPageLink.click();
        return PageFactory.initElements(webDriver, IndexPage.class);

    }
}
