package base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.*;
import report.ExtentReportManager;
import utils.ConfigReader;
import webdriver.BrowserType;
import webdriver.WebDriverManager;

import java.lang.reflect.Method;
import java.util.Objects;

public abstract class BaseTest implements ITest, ITestListener {
    protected WebDriver driver;
    static final ConfigReader configReader = new ConfigReader();

    protected static final Logger LOGGER = LogManager.getLogger(BaseTest.class.getName());
    private String testName;
    private String testDescription;
    private final ExtentReports extent = ExtentReportManager.getExtentReport();
    private ExtentTest test;

    @BeforeSuite
    public void beforeSuite(ITestContext testContext){
        String suiteName = testContext.getCurrentXmlTest().getSuite().getName();
        String projectName = configReader.getConfigKey("projectName");


    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        String testName = Objects.equals(method.getAnnotation(Test.class).testName(), "") ? method.getName() : method.getAnnotation(Test.class).testName();
        setTestName(testName);
        String testDescription = Objects.equals(method.getAnnotation(Test.class).description(), "") ? "" : method.getAnnotation(Test.class).description();
        setTestDescription(testDescription);

        test = extent.createTest(getTestName(), getTestDescription());
        test.assignCategory(method.getAnnotation(Test.class).groups());
        driver = WebDriverManager.getDriver(BrowserType.valueOf(configReader.getConfigKey("browser").toUpperCase()));
        driver.manage().window().maximize();

    }

    @AfterMethod
    public void afterMethod(ITestResult result){
        int status = result.getStatus();

        if (status == ITestResult.SUCCESS) {
            test.log(Status.PASS, "Test Passed");

        } else if (status == ITestResult.FAILURE) {
            test.log(Status.FAIL, "Test Failed");
            test.log(Status.FAIL, result.getThrowable().getMessage());
        } else if (status == ITestResult.SKIP) {
            test.log(Status.SKIP, "Test Skipped");
            test.log(Status.SKIP, result.getThrowable().getMessage());
        }

        if (driver != null) {
            WebDriverManager.quitDriver();
        }
    }
    @AfterSuite
    public void afterSuite(){
        extent.flush();
    }


    public String getTestName(){
        return testName;
    }
    public void setTestName(String testName){
        this.testName = testName;
    }

    public String getTestDescription(){
        return testDescription;
    }
    public void setTestDescription(String testDescription){
        this.testDescription = testDescription;
    }

    public ExtentTest getTest(){
        return test;
    }


}
