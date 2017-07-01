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

    public void setNewOfficeName(int officeIndex, String officeName) {
        // TODO: make it work for multiple offices
        setInputText(webDriver.findElement(By.name("newOfficeName")), officeName);
    }

    public EditBallotLayoutPage clickRenameOffice(int officeIndex) {
        webDriver.findElement(By.name("renameOffice")).click();
        return PageFactory.initElements(webDriver, EditBallotLayoutPage.class);
    }

    public IndexPage clickBackToIndexPage() {
        backToIndexPageLink.click();
        return PageFactory.initElements(webDriver, IndexPage.class);
    }
}
