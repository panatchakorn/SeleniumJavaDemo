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
    private final HashMap<String, String> configMap;

    public ConfigReader() {
        configMap = new HashMap<>();
        loadConfigProperties();
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

    public String getConfigKey(String key) {
        return configMap.getOrDefault(key, "");
    }

    public Map<String, String> getAllConfig() {
        return configMap;
    }

}
