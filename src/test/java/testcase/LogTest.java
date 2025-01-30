package testcase;

import base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LogTest extends BaseTest {

    @DataProvider(name = "numberProvider")
    public Object[][] numberProvider() {
        Object[][] data = new Object[10001][1];
        for (int i = 0; i <= 10000; i++) {
            data[i][0] = i;
        }
        return data;
    }

    @Test(dataProvider = "numberProvider")
    public void logTest1(int number) {
        System.out.println("Test1 log no " + number);
    }

    @Test(dataProvider = "numberProvider")
    public void logTest2(int number) {
        System.out.println("Test2 log no " + number);
    }
}