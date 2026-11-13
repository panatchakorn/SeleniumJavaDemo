package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Singleton ConfigReader - loads configuration once and provides global access
 * Uses double-checked locking for thread-safe lazy initialization
 */
public class ConfigReader {
    private static final Logger LOGGER = LogManager.getLogger(ConfigReader.class.getName());
    private static volatile ConfigReader instance;
    private final HashMap<String, String> configMap;

    /**
     * Private constructor to enforce singleton pattern
     */
    private ConfigReader() {
        configMap = new HashMap<>();
        loadConfigProperties();
        mergeSystemProperties();
    }

    /**
     * Get or create singleton instance (thread-safe, lazy initialization)
     * Uses double-checked locking for efficient access after first initialization
     * 
     * @return ConfigReader singleton instance
     */
    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    LOGGER.info("Initializing ConfigReader singleton instance");
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    private void loadConfigProperties() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config/config.properties")) {
            properties.load(fileInputStream);
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                configMap.put(key, value);
            }
            LOGGER.info("Successfully loaded config.properties");
        } catch (FileNotFoundException e) {
            LOGGER.warn(String.format("Config file not found: %s. Using defaults and system properties only.", System.getProperty("user.dir") + "/src/main/resources/config/config.properties"));
        } catch (IOException e) {
            LOGGER.error(String.format("Unable to load config file. Error: %s", e.getMessage()));
        }
    }

    /**
     * Merge system properties into config map.
     * System properties take precedence over config file properties.
     */
    private void mergeSystemProperties() {
        Properties systemProperties = System.getProperties();
        for (String key : systemProperties.stringPropertyNames()) {
            String value = systemProperties.getProperty(key);
            configMap.put(key, value);
            LOGGER.debug(String.format("System property override - Key: %s, Value: %s", key, value));
        }
    }

    public String getConfigKey(String key) {
        return configMap.getOrDefault(key, "");
    }

    public String getConfigKey(String key, String defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }

    public boolean getConfigKeyAsBoolean(String key) {
        return Boolean.parseBoolean(configMap.getOrDefault(key, "false"));
    }

    public boolean getConfigKeyAsBoolean(String key, boolean defaultValue) {
        String value = configMap.getOrDefault(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(value);
    }

    public Map<String, String> getAllConfig() {
        return configMap;
    }

}
