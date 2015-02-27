package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.Set;

import static info.gehrels.voting.web.integrationTests.pages.WebElementUtils.setInputText;

public final class ElectionCalculationPage {
    private final WebDriver webDriver;

    @FindBy(id="elected-female-candidates")
    WebElement electedFemaleCandidatesList;

    @FindBy(id="status-text")
    WebElement statusText;

    @FindBy(tagName = "form")
    WebElement ambiguityResolutionForm;

    public ElectionCalculationPage(final WebDriver webDriver) {
        this.webDriver = webDriver;
        new WebDriverWait(webDriver, 60).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return webDriver.findElement(By.id("status-text")).isDisplayed();
            }
        });
    }

    public boolean electionCalculationHasFinished() {
        return "Die Berechnung der Wahlergebnisse wurde erfolgreich abgeschlossen.".equals(statusText.getText());
    }

    public ElectionCalculationPage refresh() {
        webDriver.navigate().refresh();
        return reinitThisPage();
    }


    public List<String> getFemaleExclusiveElectedCandidateNames(String officeName) {
        List<WebElement> li = webDriver.findElements(By.xpath(
                "//h1[contains(text(), '" + officeName + "')]/parent::section" +
                        "/h2[contains(text(), 'Frauen')]/following-sibling::ul[1]/li"));
        Builder<String> builder = ImmutableList.builder();
        for (WebElement webElement : li) {
            builder.add(webElement.getText());
        }

        return builder.build();
    }


    public List<String> getNonFemaleExclusiveElectedCandidateNames(String officeName) {
        List<WebElement> li = webDriver.findElements(By.xpath(
                "//h1[contains(text(), '" + officeName + "')]/parent::section" +
                        "/h2[contains(text(), 'offen')]/following-sibling::ul[1]/li"));
        Builder<String> builder = ImmutableList.builder();
        for (WebElement webElement : li) {
            builder.add(webElement.getText());
        }

        return builder.build();
    }

    public ElectionCalculationPage waitForElectionCalculationToBeFinished() {
        ElectionCalculationPage electionCalculationPage = this;
        while (!electionCalculationHasFinished()) {
            electionCalculationPage = refresh();
        }
        return electionCalculationPage;
    }

    public ElectionCalculationPage waitUntilAmbiguityResolutionIsNeccessary() {
        ElectionCalculationPage electionCalculationPage = this;
        while (!ambiguityResolutionIsNeccessary()) {
            electionCalculationPage = refresh();
        }
        return electionCalculationPage;
    }

    private boolean ambiguityResolutionIsNeccessary() {
        return "Mehrere Kandidierende haben die selbe Stimmzahl. Eine Manuelle Auswahl ist notwendig.".equals(statusText.getText());
    }

    public void chooseCandidate(String candidateName) {
        WebElement element = ambiguityResolutionForm.findElement(By.xpath(".//input[@value='" + candidateName + "']"));
        element.click();
    }

    public ElectionCalculationPage submitAmbiguityResolution() {
        ambiguityResolutionForm.submit();
        return reinitThisPage();
    }

    private ElectionCalculationPage reinitThisPage() {
        return PageFactory.initElements(webDriver, ElectionCalculationPage.class);
    }

    public void setDescription(String ambiguityResolutionDescription) {
        setInputText(ambiguityResolutionForm.findElement(By.id("comment")), ambiguityResolutionDescription);
    }

    public Set<String> getWinningNonFemaleExclusiveCandidate(String officeName) {
        List<WebElement> li = getSectionOfElection(officeName)
                .findElement(By.xpath(".//h2[contains(text(), 'offen')]/following::ul[1]"))
                .findElements(By.tagName("li"));

        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (WebElement webElement : li) {
            builder.add(webElement.getText());
        }

        return builder.build();
    }

    public String getProtocol(String officeName) {
        return getSectionOfElection(officeName).findElement(By.xpath(".//pre")).getText();
    }

    private WebElement getSectionOfElection(String officeName) {
        return webDriver.findElement(By.xpath("//h1[text()='" + officeName + "']/parent::section"));
    }
}
