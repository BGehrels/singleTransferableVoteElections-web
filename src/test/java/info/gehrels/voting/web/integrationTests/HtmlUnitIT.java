package info.gehrels.voting.web.integrationTests;

import info.gehrels.voting.web.SpringConfig;
import info.gehrels.voting.web.integrationTests.pages.AdministrateBallotLayoutPage;
import info.gehrels.voting.web.integrationTests.pages.IndexPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class HtmlUnitIT {
    @Value("${local.server.port}")
    int port;

    @Test
    public void sampleTest() throws MalformedURLException {
        WebDriver driver = new FirefoxDriver();
        driver.navigate().to(new URL("http", "localhost", port, "/"));

        IndexPage indexPage = PageFactory.initElements(driver, IndexPage.class);
        AdministrateBallotLayoutPage administrateBallotLayoutPage = indexPage.clickAdministrateBallotLayoutLink();

    }
}
