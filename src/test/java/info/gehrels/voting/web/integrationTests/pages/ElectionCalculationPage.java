package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class ElectionCalculationPage {
    private final WebDriver webDriver;

    @FindBy(id="elected-female-candidates")
    WebElement electedFemaleCandidatesList;

    public ElectionCalculationPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public boolean electionCalculatinHasFinished() {
        return "Die Berechnung der Wahlergebnisse wurde erfolgreich abgeschlossen."
                .equals(webDriver.findElement(By.id("status-text")).getText());
    }

    public ElectionCalculationPage refresh() {
        webDriver.navigate().refresh();
        return PageFactory.initElements(webDriver, ElectionCalculationPage.class);
    }


    public List<String> getElectedFemaleCandidateNames(String officeName) {
        List<WebElement> li = webDriver.findElements(By.xpath(
                "//h1[contains(text(), '" + officeName + "')]/parent::section" +
                        "/h2[contains(text(), 'Frauen')]/following-sibling::ul[1]/li"));
        Builder<String> builder = ImmutableList.builder();
        for (WebElement webElement : li) {
            builder.add(webElement.getText());
        }

        return builder.build();
    }
}
