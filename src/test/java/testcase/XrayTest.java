package testcase;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class XrayTest extends BaseTest {
    @Test
    public void xrayFirstTest(){
        ExtentTest nodeSetup = getTest().createNode("Go to Selenium Dev Website");
        nodeSetup.log(Status.INFO,"Go to https://www.selenium.dev/selenium/web/web-form.html");
        //WebDriver driver = new ChromeDriver();
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(ElementNotInteractableException.class, StaleElementReferenceException.class);
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        driver.getTitle();

        ExtentTest nodeAct = getTest().createNode("Enter form and submit message");
        nodeAct.log(Status.INFO,"Enter Selenium");
        WebElement textBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("my-text")));
        WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button")));
        textBox.sendKeys("Selenium");
        submitButton.click();
        nodeAct.log(Status.INFO,"Click Submit");
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        Assert.assertEquals(message.getText(), "Received!","Message is incorrect");

    }
    @Test
    public void xraySecondTest(){
        ExtentTest nodeSetup = getTest().createNode("Go to Selenium Dev Website");
        nodeSetup.log(Status.INFO,"Go to https://www.selenium.dev/selenium/web/web-form.html");
        //WebDriver driver = new ChromeDriver();
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(ElementNotInteractableException.class, StaleElementReferenceException.class);
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        driver.getTitle();

        ExtentTest nodeAct = getTest().createNode("Enter form and submit message");
        nodeAct.log(Status.INFO,"Enter Selenium");
        WebElement textBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("my-text")));
        WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button")));
        textBox.sendKeys("Selenium");
        submitButton.click();
        nodeAct.log(Status.INFO,"Click Submit");
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        Assert.assertEquals(message.getText(), "NOT Received!","Message is incorrect");

    }

    @Test(groups = {"smoke"})
    public void xray_thirdTest(){
        ExtentTest nodeSetup = getTest().createNode("Go to Selenium Dev Website");
        nodeSetup.log(Status.INFO,"Go to https://www.selenium.dev/selenium/web/web-form.html");
        //WebDriver driver = new ChromeDriver();
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(300))
                .ignoring(ElementNotInteractableException.class, StaleElementReferenceException.class);
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        driver.getTitle();

        ExtentTest nodeAct = getTest().createNode("Enter form and submit message");
        nodeAct.log(Status.INFO,"Enter Selenium");
        WebElement textBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("my-text")));
        WebElement submitButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button")));
        textBox.sendKeys("Selenium");
        submitButton.click();
        nodeAct.log(Status.INFO,"Click Submit");
        WebElement message = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("message")));
        Assert.assertEquals(message.getText(), "NOT Received!","Message is incorrect");

    }

}
