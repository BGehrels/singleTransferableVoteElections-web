package info.gehrels.voting.web.integrationTests;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class WebDriverRule extends TestWatcher {
    private static final Logger LOG = LoggerFactory.getLogger(WebDriverRule.class);

    private WebDriver driver;

    @Override
    protected void starting(Description description) {
        driver = new HtmlUnitDriver();
    }

    @Override
    protected void failed(Throwable e, Description description) {
        LOG.error("WebDriver test {} failed with page source {}", description, driver.getPageSource());
    }

    @Override
    protected void finished(Description description) {
        driver.close();
    }

    public WebDriver getDriver() {
        return driver;
    }
}
