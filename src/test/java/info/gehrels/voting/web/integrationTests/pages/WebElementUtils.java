package info.gehrels.voting.web.integrationTests.pages;

import org.openqa.selenium.WebElement;

import java.util.List;

public class WebElementUtils {
    public static WebElement findByValue(List<WebElement> radioButtons, String value) {
        for (WebElement radioButton : radioButtons) {
            if (radioButton.getAttribute("value").equals(value)) {
                return radioButton;
            }
        }

        throw new IllegalStateException("There is no radio button with value " + value + " in " + radioButtons);
    }

    public static WebElement setInputText(WebElement webElement, String text) {
        webElement.clear();
        webElement.click();
        webElement.sendKeys(text);
        return webElement;
    }
}
