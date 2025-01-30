package testcase;

import base.BaseTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.ConfigReader;

public class LogTest extends BaseTest {
    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class.getName());

   @BeforeMethod
    public void beforeMethod() {
        LOGGER.info("Before method");
    }
    @AfterMethod
    public void afterMethod() {
        LOGGER.info("After method");
    }
    @DataProvider(name = "numberProvider")
    public Object[][] numberProvider() {
        Object[][] data = new Object[20001][1];
        for (int i = 0; i <= 20000; i++) {
            data[i][0] = i;
        }
        return data;
    }

    @Test(dataProvider = "numberProvider")
    public void logTest1(int number) {
        String lineNo = "Line no " + number;
        LOGGER.error("Test1 log {}",  lineNo);
        Assert.assertEquals("Line no " + number, lineNo);

    }

    @Test(dataProvider = "numberProvider")
    public void logTest2(int number) {
        String lineNo = "Line no " + number;
        LOGGER.error("Test2 log {}",  lineNo);
        Assert.assertEquals("Line no " + number, lineNo);
    }
}
