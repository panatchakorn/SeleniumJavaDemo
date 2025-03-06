package utils;

import com.google.gson.reflect.TypeToken;

import java.util.List;

public class DynamicParser {
    public static Object parseDynamicJson(String filePath) {
        // Example code: you can enhance this further
        List<Object> dataList = GenericJsonParser.parseJson(filePath, new TypeToken<List<Object>>() {}.getType());
        return dataList;
    }



}

