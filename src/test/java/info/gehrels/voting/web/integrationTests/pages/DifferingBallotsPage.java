package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;
import java.util.Set;

public final class DifferingBallotsPage {
    @FindBy(xpath = "//h2[text()='Bei der Ersteingabe mehrfach eingegebene Stimmzettel']/parent::section")
    private WebElement firstTryDuplicatesSection;

    @FindBy(xpath = "//h2[text()='Bei der Kontrolleingabe mehrfach eingegebene Stimmzettel']/parent::section")
    private WebElement secondTryDuplicatesSection;

    @FindBy(xpath = "//h2[text()='Stimmzettel der Ersteingabe, die bei der Kontrolleingabe ausgelassen wurden']/parent::section")
    private WebElement onlyInFirstTry;

    @FindBy(xpath = "//h2[text()='Stimmzettel der Kontrolleingabe, die bei der Ersteingabe ausgelassen wurden']/parent::section")
    private WebElement onlyInSecondTry;

    @FindBy(xpath = "//h2[text()='Stimmzettel bei denen sich die Ersteingabe und die Kontrolleingabe unterscheiden']/parent::section")
    private WebElement differentBetweenFirstAndSecondTry;

    @FindBy(xpath = "//input[@type='submit']")
    private WebElement deleteAllConflictingBallots;

    private final WebDriver webDriver;

    public DifferingBallotsPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public IndexPage clickDeleteAllConflictingBallots() {
        deleteAllConflictingBallots.click();
        return PageFactory.initElements(webDriver, IndexPage.class);
    }

    public Set<Integer> getFirstTryDuplicates() {
        return getBallotIds(firstTryDuplicatesSection);
    }

    public Set<Integer> getSecondTryDuplicates() {
       return getBallotIds(secondTryDuplicatesSection);
    }

    public Set<Integer> getOnlyInFirstTry() {
        return getBallotIds(onlyInFirstTry);
    }

    public Set<Integer> getOnlyInSecondTry() {
        return getBallotIds(onlyInSecondTry);
    }

    public Set<Integer> getDifferentBetweenFirstAndSecondTry() {
        return getBallotIds(differentBetweenFirstAndSecondTry);
    }

    private Set<Integer> getBallotIds(WebElement section) {
        Builder<Integer> builder = ImmutableSet.builder();
        List<WebElement> elements = section.findElements(By.xpath(".//li"));
        for (WebElement element : elements) {
            builder.add(Integer.valueOf(element.getText().replace("Nr. ", "")));
        }
        return builder.build();
    }
}
