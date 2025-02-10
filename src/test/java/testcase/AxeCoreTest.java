package testcase;


import com.deque.html.axecore.results.ResultType;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.selenium.AxeReporter;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ConfigReader;
import webdriver.BrowserType;
import webdriver.WebDriverManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.deque.html.axecore.selenium.AxeReporter.getReadableAxeResults;


public class AxeCoreTest {
    WebDriver driver;
    static final ConfigReader configReader = new ConfigReader();

    @BeforeMethod
    public void setup() {
//        ExtentTest nodeSetup = getTest().createNode("Go to Selenium Dev Website");
//        nodeSetup.log(Status.INFO,"Go to https://www.selenium.dev/selenium/web/web-form.html");
        //WebDriver driver = new ChromeDriver();

        driver = WebDriverManager.getDriver(BrowserType.valueOf(configReader.getConfigKey("browser")
                .toUpperCase()));
        driver.manage()
                .window()
                .maximize();

        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(ElementNotInteractableException.class, StaleElementReferenceException.class);
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        driver.getTitle();


    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            WebDriverManager.quitDriver();
        }
    }

    @Test
    public void checkAccessibility() {
//        ExtentTest nodeAct = getTest().createNode("Check Accessibility");
//        nodeAct.log(Status.INFO,"Enter Selenium");
        String testName = "Web Form Page";
        AxeBuilder builder = makeAxeBuilder();
//        builder.withTags(Arrays.asList("wcag2aa", "wcag2aaa", "wcag21a", "wcag21aa", "wcag22aa", "best-practice", "ACT"))
//                .exclude("#comonly-released-element-with-known-issues");
        Results result = builder.analyze(driver);
        printResults(result, testName);
        createReport(result, testName);

        Assert.assertTrue(result.violationFree());
    }

    private AxeBuilder makeAxeBuilder() {
        return new AxeBuilder()
                .withTags(Arrays.asList("wcag2a", "wcag2aa", "wcag2aaa", "wcag21a", "wcag21aa", "wcag22aa", "best-practice", "ACT"))
                .exclude("#comonly-released-element-with-known-issues");
    }

    private void printResults(Results results, String testName) {
        System.out.println("Test Name: " + testName);
        System.out.println("Current Page URL: " + driver.getCurrentUrl());
        System.out.println("Accessibility VIOLATIONS count: " + results.getViolations()
                .size());
        System.out.println("Accessibility violations: " + results.getViolations());
        System.out.println("------------------------------------");
        System.out.println("Accessibility PASSES count: " + results.getPasses()
                .size());
        System.out.println("Accessibility passes: " + results.getPasses());
        System.out.println("------------------------------------");
        System.out.println("Accessibility INCOMPLETE count: " + results.getIncomplete()
                .size());
        System.out.println("Accessibility incomplete: " + results.getIncomplete());
        System.out.println("------------------------------------");
        System.out.println("Accessibility INAPPLICABLE count: " + results.getInapplicable()
                .size());
        System.out.println("Accessibility inapplicable: " + results.getInapplicable());
    }

    private void createReport(Results results, String testName) {
        testName = testName.trim().replace(" ", "_").toLowerCase();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmssSSS");
        String timestamp = LocalDateTime.now()
                .format(formatter);
        String fileName = testName + "_" + timestamp;
        String filePath = "target/reports/axe/" + fileName;

        try {
            Path reportDir = Paths.get("target", "reports", "axe");
            if (!Files.exists(reportDir)) {
                Files.createDirectories(reportDir);
            }

           /* Path filePath = reportDir.resolve(fileName);
            Files.createFile(filePath);*/
            AxeReporter.writeResultsToJsonFile(filePath,results);

            // Write only violations to text file
            List<Rule> violatedRules = results.getViolations();
            getReadableAxeResults(ResultType.Violations.getKey(),driver,violatedRules);
            AxeReporter.writeResultsToTextFile(filePath + "violation",AxeReporter.getAxeResultString());

            // Write passed rules check to text file
            List<Rule> passedRules = results.getPasses();
            getReadableAxeResults(ResultType.Passes.getKey(),driver,passedRules);
            AxeReporter.writeResultsToTextFile(filePath + "passed",AxeReporter.getAxeResultString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
