package webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.MalformedURLException;

import java.net.URL;

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

    public static WebDriver createRemoteDriver(BrowserType browserType, String remoteUrl) {
        try {
            URL url = new URL(remoteUrl);
            return switch (browserType) {
                case CHROME -> new RemoteWebDriver(url, BrowserOptions.getChromeOptions());
                case FIREFOX -> new RemoteWebDriver(url, BrowserOptions.getFirefoxOptions());
                case EDGE -> new RemoteWebDriver(url, BrowserOptions.getEdgeOptions());
                default -> throw new IllegalArgumentException("Unsupported browser type: " + browserType);
            };
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid remote URL: " + remoteUrl, e);
        }
    }
}