package testcase;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.browserstack.BrowserStackSdk;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.HashMap;

public class BrowserStackTest extends BaseTest {
    @Test
    public void browserstackFirstTest() {
        ExtentTest nodeBrowserStack = getTest().createNode("BrowserStack details");
        HashMap<String, Object> platform = BrowserStackSdk.getCurrentPlatform();
        platform.forEach((key, value) -> {
            System.out.println(key + " : " + value);
            nodeBrowserStack.info(key + " : " + value);
        });

        ExtentTest nodeSetup = getTest().createNode("Go to Selenium Dev Website");
        nodeSetup.log(Status.INFO, "Go to https://www.selenium.dev/selenium/web/web-form.html");
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(ElementNotInteractableException.class, StaleElementReferenceException.class);
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        driver.getTitle();

        ExtentTest nodeAct = getTest().createNode("Enter form and submit message");
        nodeAct.log(Status.INFO, "Enter Selenium BrowserStack");
        WebElement textBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("my-text")));
        WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button")));
        textBox.sendKeys("Selenium BrowserStack");
        submitButton.click();
        nodeAct.log(Status.INFO, "Click Submit");
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        Assert.assertEquals(message.getText(), "Received!", "Message is incorrect");

    }
}