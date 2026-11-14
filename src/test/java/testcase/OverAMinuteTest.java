package testcase;

import base.BaseTest;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class OverAMinuteTest extends BaseTest {
    @Test
    public void testRunsOverOneMinute() throws InterruptedException {
        ExtentTest nodeSetup = getTest().createNode("Make the test run over one minute");
        long startTime = System.nanoTime();
        Instant startedAt = Instant.now();
        LOGGER.info("Test started at: " + startedAt);
        nodeSetup.log(Status.INFO, "Test started at: " + startedAt);

        Thread.sleep(TimeUnit.SECONDS.toMillis(65));

        long endTime = System.nanoTime();
        Instant endedAt = Instant.now();
        LOGGER.info("Test ended at: " + endedAt);
        nodeSetup.log(Status.INFO, "Test ended at: " + endedAt);

        long elapsedNanos = endTime - startTime;
        Assert.assertTrue(
                elapsedNanos > TimeUnit.MINUTES.toNanos(1),
                "Test should run for more than one minute. Actual duration: "
                        + TimeUnit.NANOSECONDS.toSeconds(elapsedNanos) + " seconds"
        );
    }
}
