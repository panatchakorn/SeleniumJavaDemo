package utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigReader {
    private static final Logger LOGGER = LogManager.getLogger(ConfigReader.class.getName());
    private static volatile ConfigReader instance;
    private final HashMap<String, String> configMap;

    public ConfigReader() {
        configMap = new HashMap<>();
        loadConfigProperties();
        mergeSystemProperties();
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    LOGGER.info("Loading ConfigReader singleton instance");
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
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

    private void loadConfigProperties() {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config/config.properties")) {
            properties.load(fileInputStream);
            for (String key : properties.stringPropertyNames()) {
                String value = properties.getProperty(key);
                configMap.put(key, value);
            }
        } catch (
                FileNotFoundException e) {
            LOGGER.error(String.format("Unable to find config file %s. Error: %s", System.getProperty("user.dir") + "/src/main/resources/config/config.properties", e.getMessage()));
        } catch (
                IOException e) {
            LOGGER.error(String.format("Unable to load config file. Error: %s", e.getMessage()));
        }
    }
    // System properties take precedence over config file properties

    private void mergeSystemProperties() {
        Properties systemProperties = System.getProperties();
        for (String key : systemProperties.stringPropertyNames()) {
            String value = systemProperties.getProperty(key);
            configMap.put(key, value);
            LOGGER.debug(String.format("System property override - Key %s=%s", key, value));
        }
        System.getProperties().putAll(configMap);
    }

}
