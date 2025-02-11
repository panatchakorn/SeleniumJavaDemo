package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class GeneralUtil {
    private GeneralUtil() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String convertStackTraceToString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}