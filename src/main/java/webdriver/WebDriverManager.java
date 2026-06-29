package webdriver;

import org.openqa.selenium.WebDriver;
import utils.ConfigReader;

public class WebDriverManager {
    private WebDriverManager() {
        throw new IllegalStateException("WebDriverManager class cannot be instantiated");
    }

    private static final ThreadLocal<WebDriver> webDriverThreadLocal = new ThreadLocal<>();

    public static WebDriver getDriver(BrowserType browserType) {
        WebDriver driver = webDriverThreadLocal.get();

        if (driver == null) {
            ConfigReader configReader = ConfigReader.getInstance();
            boolean isRemote = configReader.getConfigKeyAsBoolean("remote", false);

            if (isRemote) {
                String remoteUrl = configReader.getConfigKey("grid.url");
                driver = DriverFactory.createRemoteDriver(browserType, remoteUrl);
            } else {
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