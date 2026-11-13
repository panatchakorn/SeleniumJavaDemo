package webdriver;

/**
 * Configuration class for remote webdriver connections
 */
public class RemoteWebDriverConfig {
    private static final String DEFAULT_GRID_URL = "http://localhost:4444";
    private static final String BROWSERSTACK_URL = "https://hub.browserstack.com/wd/hub";

    private RemoteWebDriverConfig() {
        throw new IllegalStateException("RemoteWebDriverConfig class cannot be instantiated");
    }

    /**
     * Get the default Selenium Grid URL
     * @return Default grid URL (http://localhost:4444)
     */
    public static String getDefaultGridUrl() {
        return DEFAULT_GRID_URL;
    }

    /**
     * Get the BrowserStack remote URL
     * @return BrowserStack URL
     */
    public static String getBrowserStackUrl() {
        return BROWSERSTACK_URL;
    }

    /**
     * Get the grid URL from system property or use default
     * @return Grid URL from system property or default
     */
    public static String getGridUrlFromConfig() {
        return System.getProperty("grid.url", DEFAULT_GRID_URL);
    }

    /**
     * Get the BrowserStack URL with credentials
     * @param username BrowserStack username
     * @param accessKey BrowserStack access key
     * @return BrowserStack URL with credentials
     */
    public static String getBrowserStackUrl(String username, String accessKey) {
        return String.format("https://%s:%s@hub.browserstack.com/wd/hub", username, accessKey);
    }
}
