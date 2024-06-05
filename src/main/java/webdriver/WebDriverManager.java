package webdriver;

import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

import java.net.MalformedURLException;

public class WebDriverManager {
    private WebDriverManager() {
        throw new IllegalStateException("WebDriverManager class cannot be instantiated");
    }

    private static final ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();
    static final ConfigReader configReader = new ConfigReader();

    public static WebDriver getDriver(BrowserType browserType) throws MalformedURLException {
        WebDriver driver = webDriverThreadLocal.get();

        if (driver == null) {
            if (configReader.getConfigKey("runOnGrid").equalsIgnoreCase("true")) {
                // RemoteWebDriver
                try {
                    driver = DriverFactory.createRemoteWebDriver(browserType);
                } catch (MalformedURLException e) {
                    throw new MalformedURLException("Invalid Selenium grid url: " + e);
                }
            } else {
                // Local WebDriver
                driver = DriverFactory.createDriver(browserType);
            }

            webDriverThreadLocal.set(driver);
        }

        return driver;
    }

    public static void quitDriver() {
        WebDriver driver = webDriverThreadLocal.get();

        if (driver != null) {
            driver.quit();
            webDriverThreadLocal.remove();
        }
    }
}