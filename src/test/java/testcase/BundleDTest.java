package testcase;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class BundleDTest extends BaseTest {
    @Test
    public void bundleDPassedTest(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Passed");
        nodeSetup.log(Status.INFO,"Check if true is true");
        Assert.assertEquals(true,true,"This test should passed");
    }
    @Test
    public void bundleDSkippedTest(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Skipped");
        nodeSetup.log(Status.INFO,"Check for test skipped");
        ITestResult result = Reporter.getCurrentTestResult();
        result.setStatus(ITestResult.SKIP);
        result.setThrowable(new SkipException("This test should skipped"));
    }
    @Test
    public void bundleDFailed1Test(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Failed");
        nodeSetup.log(Status.INFO,"Check for test failed");
        Assert.assertEquals(false,true,"This test should failed");
    }

    @Test
    public void bundleDFailed2Test(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Failed");
        nodeSetup.log(Status.INFO,"Check for test failed");
        Assert.assertEquals(false,true,"This test should failed");
    }
}
