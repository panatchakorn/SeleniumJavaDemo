package report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Protocol;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ExtentReportManager {
    private ExtentReportManager() { throw new IllegalStateException("ExtentReportManager cannot be instantiated"); }
    private static final ThreadLocal<ExtentReports> extentReports = ThreadLocal.withInitial(() -> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/reports/extent-report-" + timestamp + ".html");

        sparkReporter.config()
                .setDocumentTitle("Automation Test Report");
        sparkReporter.config()
                .setReportName("SeleniumJavaDomo Project");
        sparkReporter.config()
                .setEncoding("utf-8");
        sparkReporter.config()
                .setProtocol(Protocol.HTTPS);
        sparkReporter.config()
                .setTimelineEnabled(true);
        sparkReporter.config()
                .setTheme(Theme.DARK);
        sparkReporter.config()
                .setCss("body { background-color: #f2f2f2; } .test-name { font-weight: bold; }");

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        extent.setSystemInfo("Application", "Selenium Website");
        extent.setSystemInfo("Operating System", System.getProperty("os.name"));
        extent.setSystemInfo("User", System.getProperty("user.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Environment", "Prod");

        return extent;
    });

    public static ExtentReports getExtentReport() {
        return extentReports.get();
    }

    public static void removeExtentReport() {
        extentReports.remove();
    }
}
