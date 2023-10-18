package webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DriverFactory {
    private DriverFactory() {
        throw new IllegalStateException("DriverFactory class cannot be instantiated");
    }

    public static WebDriver createDriver(BrowserType browserType) {
       return switch (browserType) {
            case CHROME -> new ChromeDriver(BrowserOptions.getChromeOptions());
            case FIREFOX -> new FirefoxDriver(BrowserOptions.getFirefoxOptions());
            case EDGE -> new EdgeDriver(BrowserOptions.getEdgeOptions());
            default -> throw new IllegalArgumentException("Unsupported browser type: " + browserType);
        };
    }
}