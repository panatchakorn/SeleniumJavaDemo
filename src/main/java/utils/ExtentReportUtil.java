package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtentReportUtil {

    private static final Logger LOGGER = LogManager.getLogger(ExtentReportUtil.class);

    private ExtentReportUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void logJsonData(ExtentTest test, Object jsonDataObject) {
        String dataString;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            dataString = objectMapper.writeValueAsString(jsonDataObject);
        } catch (Exception e) {
            dataString = GeneralUtil.convertStackTraceToString(e);
            LOGGER.error("Failed to log JSON data {}", dataString);
        }
        test.info(MarkupHelper.createCodeBlock(dataString, CodeLanguage.JSON));
    }

    public static void logJsonData(ExtentTest test, String jsonString) {
        test.info(MarkupHelper.createCodeBlock(jsonString, CodeLanguage.JSON));
    }
}