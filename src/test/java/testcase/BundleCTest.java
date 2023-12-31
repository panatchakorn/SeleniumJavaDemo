package testcase;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

public class BundleCTest extends BaseTest {
    @Test
    public void bundleCPassed1Test(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Passed");
        nodeSetup.log(Status.INFO,"Check if true is true");
        Assert.assertEquals(true,true,"This test should passed");
    }
    @Test
    public void bundleCPassed2Test(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Passed");
        nodeSetup.log(Status.INFO,"Check if true is true");
        Assert.assertEquals(true,true,"This test should passed");
    }
    @Test @Ignore
    public void bundleCSkippedTest(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Skipped");
        nodeSetup.log(Status.INFO,"Check for test skipped");
        ITestResult result = Reporter.getCurrentTestResult();
        result.setStatus(ITestResult.SKIP);
        result.setThrowable(new SkipException("This test should skipped"));
    }
    @Test @Ignore
    public void bundleCFailedTest(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Failed");
        nodeSetup.log(Status.INFO,"Check for test failed");
        Assert.assertEquals(false,true,"This test should failed");
    }
}
