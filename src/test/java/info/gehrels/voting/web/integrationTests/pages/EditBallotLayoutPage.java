package info.gehrels.voting.web.integrationTests.pages;

import com.google.common.base.Predicate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import static info.gehrels.voting.web.integrationTests.pages.WebElementUtils.setInputText;

public final class EditBallotLayoutPage {
    private final WebDriver webDriver;

    @FindBy(linkText = "Zurück zur Startseite")
    private WebElement backToIndexPageLink;

    public EditBallotLayoutPage(final WebDriver webDriver) {
        this.webDriver = webDriver;

        new WebDriverWait(webDriver, 60).until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(WebDriver input) {
                return webDriver.findElement(By.linkText("Zurück zur Startseite")).isDisplayed();
            }
        });
    }

    public void setNewOfficeName(String oldOfficeName, String newOfficeName) {
        By xpath = By.xpath("//section/h2[text()='" + oldOfficeName + "']/following-sibling::form/input[@name='newOfficeName']");
        setInputText(webDriver.findElement(xpath), newOfficeName);
    }

    public EditBallotLayoutPage clickRenameOffice(String oldOfficeName) {
        By xpath = By.xpath("//section/h2[text()='" + oldOfficeName + "']/following-sibling::form/input[@name='renameOffice']");

        webDriver.findElement(xpath).click();
        return PageFactory.initElements(webDriver, EditBallotLayoutPage.class);
    }

    public IndexPage clickBackToIndexPage() {
        backToIndexPageLink.click();
        return PageFactory.initElements(webDriver, IndexPage.class);
    }

    public void setNewNumberOfFemaleExclusivePositions(String officeName, int numberOfPositions) {
        By xpath = By.xpath("//section/h2[text()='" + officeName + "']/following-sibling::form/input[@name='newNumberOfFemaleOnlyPositions']");
        webDriver.findElement(xpath).clear();
        webDriver.findElement(xpath).sendKeys(Integer.toString(numberOfPositions));
    }

    public EditBallotLayoutPage clickChangeNumberOfFemaleExclusivePositions(String officeName) {
        By xpath = By.xpath("//section/h2[text()='" + officeName + "']/following-sibling::form/input[@name='changeNumberOfFemaleOnlyPositions']");
        webDriver.findElement(xpath).click();
        return PageFactory.initElements(webDriver, EditBallotLayoutPage.class);
    }
}
