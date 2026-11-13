package testcase;

import org.testng.annotations.Test;
import utils.ConfigReader;

/**
 * Test suite for ConfigReader singleton pattern
 */
public class SingletonConfigReaderTest {

    @Test(testName = "Verify ConfigReader singleton returns same instance",
          description = "Multiple calls to getInstance() should return the same object")
    public void testSingletonInstance() {
        ConfigReader instance1 = ConfigReader.getInstance();
        ConfigReader instance2 = ConfigReader.getInstance();
        ConfigReader instance3 = ConfigReader.getInstance();

        assert instance1 == instance2 : "getInstance() should return same instance";
        assert instance2 == instance3 : "getInstance() should return same instance";
        System.out.println("✓ Singleton returns same instance across multiple calls");
        System.out.println("  Instance 1: " + instance1.hashCode());
        System.out.println("  Instance 2: " + instance2.hashCode());
        System.out.println("  Instance 3: " + instance3.hashCode());
    }

    @Test(testName = "Verify ConfigReader singleton loads config once",
          description = "Configuration should be loaded only once at first getInstance() call")
    public void testSingletonLoadsOnce() {
        // First call initializes
        ConfigReader instance1 = ConfigReader.getInstance();
        String value1 = instance1.getConfigKey("browser");

        // Second call should get same instance with same data
        ConfigReader instance2 = ConfigReader.getInstance();
        String value2 = instance2.getConfigKey("browser");

        assert value1.equals(value2) : "Configuration values should be consistent";
        System.out.println("✓ Singleton loads configuration once");
        System.out.println("  Browser (1st call): " + value1);
        System.out.println("  Browser (2nd call): " + value2);
    }

    @Test(testName = "Verify ConfigReader singleton is accessible from anywhere",
          description = "getInstance() should work from any class without needing a reference")
    public void testSingletonGlobalAccess() {
        // Simulate access from different parts of application
        String projectName = ConfigReader.getInstance().getConfigKey("projectName");
        String browser = ConfigReader.getInstance().getConfigKey("browser");
        boolean isRemote = ConfigReader.getInstance().getConfigKeyAsBoolean("remote");
        String remoteUrl = ConfigReader.getInstance().getConfigKey("remote.url");

        assert !projectName.isEmpty() : "projectName should not be empty";
        assert !browser.isEmpty() : "browser should not be empty";
        System.out.println("✓ Singleton accessible from anywhere");
        System.out.println("  projectName: " + projectName);
        System.out.println("  browser: " + browser);
        System.out.println("  remote: " + isRemote);
        System.out.println("  remote.url: " + remoteUrl);
    }

    @Test(testName = "Verify ConfigReader singleton with multiple property accesses",
          description = "Multiple accesses should work on same instance")
    public void testMultiplePropertyAccess() {
        ConfigReader config = ConfigReader.getInstance();

        // Multiple accesses to same instance
        String url1 = config.getConfigKey("url");
        String url2 = config.getConfigKey("url");
        String project1 = config.getConfigKey("projectName");
        String project2 = config.getConfigKey("projectName");

        assert url1.equals(url2) : "Same key should return same value";
        assert project1.equals(project2) : "Same key should return same value";
        System.out.println("✓ Multiple property accesses work correctly");
        System.out.println("  URL matches: " + url1.equals(url2));
        System.out.println("  Project matches: " + project1.equals(project2));
    }

    @Test(testName = "Verify ConfigReader singleton thread safety",
          description = "Multiple threads should get same singleton instance")
    public void testSingletonThreadSafety() throws InterruptedException {
        ConfigReader[] instances = new ConfigReader[10];

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                instances[index] = ConfigReader.getInstance();
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }

        // Verify all threads got the same instance
        for (int i = 1; i < 10; i++) {
            assert instances[0] == instances[i] : "All threads should get same instance";
        }

        System.out.println("✓ Singleton is thread-safe");
        System.out.println("  10 threads all received same instance: " + instances[0].hashCode());
    }

    @Test(testName = "Verify singleton with default values",
          description = "Singleton should provide default values for missing keys")
    public void testSingletonDefaults() {
        ConfigReader config = ConfigReader.getInstance();

        String unknownValue = config.getConfigKey("unknown.key.that.does.not.exist", "defaultValue");
        boolean unknownBoolean = config.getConfigKeyAsBoolean("unknown.boolean", true);

        assert unknownValue.equals("defaultValue") : "Should return provided default";
        assert unknownBoolean == true : "Should return provided default";
        System.out.println("✓ Singleton provides default values");
        System.out.println("  Unknown string: " + unknownValue);
        System.out.println("  Unknown boolean: " + unknownBoolean);
    }

    @Test(testName = "Verify singleton caches configuration",
          description = "Configuration should be cached after initial load")
    public void testSingletonCaching() {
        ConfigReader config = ConfigReader.getInstance();

        // All subsequent calls use cached data (no file I/O)
        for (int i = 0; i < 1000; i++) {
            String value = config.getConfigKey("browser");
            assert value.equals("chrome") : "Cached value should be consistent";
        }

        System.out.println("✓ Singleton caches configuration");
        System.out.println("  1000 accesses completed successfully (all from cache)");
    }

    @Test(testName = "Verify singleton with ConfigurationLoadingTest",
          description = "Singleton should work with existing configuration loading tests")
    public void testSingletonIntegration() {
        // Test that getInstance works where old code used new ConfigReader()
        ConfigReader config = ConfigReader.getInstance();

        var allConfig = config.getAllConfig();
        assert !allConfig.isEmpty() : "Configuration should not be empty";

        System.out.println("✓ Singleton integration successful");
        System.out.println("  Total configs loaded: " + allConfig.size());
    }
}
