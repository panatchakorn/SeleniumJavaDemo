package testcase;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SecondTest extends BaseTest {
    @Test
    public void secondLotPassed1Test(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Passed 1 ");
        nodeSetup.log(Status.INFO,"Check if true is true");
        Assert.assertEquals(true,true,"This test should passed1 ");
    }
    @Test
    public void secondLotPassed2Test(){
        ExtentTest nodeSetup = getTest().createNode("Make the test Passed 2 ");
        nodeSetup.log(Status.INFO,"Check if true is true");
        Assert.assertEquals(true,true,"This test should passed2");
    }
}
