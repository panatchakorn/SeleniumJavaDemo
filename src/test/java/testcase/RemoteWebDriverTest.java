package testcase;

import org.openqa.selenium.WebDriver;
import webdriver.BrowserType;
import webdriver.WebDriverManager;

/**
 * Sample test demonstrating remote webdriver usage via system properties
 */
public class RemoteWebDriverTest {

    public static void main(String[] args) {
        // Example 1: Local WebDriver (default)
        localWebDriverExample();

        // Example 2: Remote WebDriver via system properties
        // remoteWebDriverExample();
    }

    public static void localWebDriverExample() {
        WebDriver driver = null;
        try {
            System.out.println("=== Local WebDriver Example ===");
            System.out.println("System property 'remote' is: " + System.getProperty("remote", "not set (defaults to false)"));
            
            // WebDriverManager checks system property 'remote'
            // Since 'remote' is false/not set, it uses local WebDriver
            driver = WebDriverManager.getDriver(BrowserType.CHROME);

            driver.navigate().to("https://www.google.com");
            System.out.println("✓ Local WebDriver connected successfully");
            System.out.println("✓ Page title: " + driver.getTitle());

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                WebDriverManager.quitDriver();
            }
        }
    }

    public static void remoteWebDriverExample() {
        WebDriver driver = null;
        try {
            System.out.println("=== Remote WebDriver Example ===");
            System.out.println("System property 'remote' is: " + System.getProperty("remote", "false"));
            System.out.println("System property 'remote.url' is: " + System.getProperty("remote.url", "http://localhost:4444"));
            
            // WebDriverManager checks system property 'remote'
            // If 'remote=true', it uses RemoteWebDriver with the specified URL
            driver = WebDriverManager.getDriver(BrowserType.CHROME);

            driver.navigate().to("https://www.google.com");
            System.out.println("✓ Remote WebDriver connected successfully");
            System.out.println("✓ Page title: " + driver.getTitle());

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                WebDriverManager.quitDriver();
            }
        }
    }
}

