package testcase;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.deque.html.axecore.extensions.WebDriverExtensions;
import com.deque.html.axecore.results.ResultType;
import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.results.Rule;
import com.deque.html.axecore.selenium.AxeBuilder;
import com.deque.html.axecore.selenium.AxeReporter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import utils.ExtentReportUtil;
import utils.GeneralUtil;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static com.deque.html.axecore.selenium.AxeReporter.getReadableAxeResults;


public class AxeCoreTest extends BaseTest {

    @BeforeMethod
    public void setup() {
        String url = getConfigKey("url");
        ExtentTest nodeSetup = getTest().createNode("Go to Selenium Dev Website");
        nodeSetup.log(Status.INFO, "Go to " + url);
        driver.get(url);
    }

    @Test
    public void checkAccessibilityWithWebDriverExtensions() throws OperationNotSupportedException, IOException {
        ExtentTest nodeAct = getTest().createNode("Analyse with Axe-Core");

        String testName = "Web Form Page";
        Results result = WebDriverExtensions.analyze(driver);
        printResults(result, testName);
        ObjectWriter writer = new ObjectMapper().writer()
                .withDefaultPrettyPrinter();
        String jsonReport = writer.writeValueAsString(result);
        ExtentReportUtil.logJsonData(nodeAct, jsonReport);
        Assert.assertTrue(result.violationFree(), "Accessibility violations should not be found.");
    }

    @Test
    public void checkAccessibility() {
        ExtentTest nodeAct = getTest().createNode("Analyse with Axe-Core");
        String testName = "Web Form Page";
        AxeBuilder builder = makeAxeBuilder();
//        builder.withTags(Arrays.asList("wcag2aa", "wcag2aaa", "wcag21a", "wcag21aa", "wcag22aa", "best-practice", "ACT"))
//                .exclude("#comonly-released-element-with-known-issues");
        Results result = builder.analyze(driver);
        printResults(result, testName);
        createReport(result, testName);
        ExtentReportUtil.logJsonData(nodeAct, result);

        Assert.assertTrue(result.violationFree(),"Accessibility violations should not be found.");
    }

    private AxeBuilder makeAxeBuilder() {
        return new AxeBuilder()
                .withTags(Arrays.asList("wcag2a","wcag2aa","wcag2aaa","wcag21aa"))
//                .withTags(Arrays.asList("wcag2a", "wcag2aa", "wcag2aaa", "wcag21a", "wcag21aa", "wcag22aa", "best-practice", "ACT"))
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
        testName = testName.trim()
                .replace(" ", "_")
                .toLowerCase();
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
            AxeReporter.writeResultsToJsonFile(filePath, results);

            // Write only violations to text file
            List<Rule> violatedRules = results.getViolations();
            getReadableAxeResults(ResultType.Violations.getKey(), driver, violatedRules);
            AxeReporter.writeResultsToTextFile(filePath + "violation", AxeReporter.getAxeResultString());

            // Write passed rules check to text file
            List<Rule> passedRules = results.getPasses();
            getReadableAxeResults(ResultType.Passes.getKey(), driver, passedRules);
            AxeReporter.writeResultsToTextFile(filePath + "passed", AxeReporter.getAxeResultString());

            // Write inapplicable rules check to text file
            List<Rule> inapplicableRules = results.getInapplicable();
            getReadableAxeResults(ResultType.Inapplicable.getKey(), driver, inapplicableRules);
            AxeReporter.writeResultsToTextFile(filePath + "inapplicable", AxeReporter.getAxeResultString());

            // Write incomplete rules check to text file
            List<Rule> incompleteRules = results.getIncomplete();
            getReadableAxeResults(ResultType.Inapplicable.getKey(), driver, incompleteRules);
            AxeReporter.writeResultsToTextFile(filePath + "incomplete", AxeReporter.getAxeResultString());

        } catch (IOException e) {
            LOGGER.error(GeneralUtil.convertStackTraceToString(e));
        }

    }
}
