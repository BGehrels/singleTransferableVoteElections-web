package info.gehrels.voting.web.integrationTests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class LoginHelper {
    private LoginHelper() {}

    public static void login(WebDriver driver, String baseUrl, String username, String password) {
        driver.get(baseUrl + "/login");
        driver.findElement(By.name("username")).sendKeys(username);
        driver.findElement(By.name("password")).sendKeys(password);
        driver.findElement(By.tagName("button")).click();
    }
}