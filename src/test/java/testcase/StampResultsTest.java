package testcase;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.time.Duration;

public class StampResultsTest extends BaseTest {
    @Test
    public void passedTest(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Passed");
        nodeSetup.log(Status.INFO,"Check if true is true");
        Assert.assertEquals(true,true,"This test should passed");
    }
    @Test
    public void skippedTest(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Skipped");
        nodeSetup.log(Status.INFO,"Check for terst skipped");
        throw new SkipException("This test should skipped");
    }
    @Test
    public void failedTest(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Failed");
        nodeSetup.log(Status.INFO,"Check for test failed");
        Assert.fail("This test should failed");
    }
}
