package datamodel;

import utils.GenericJsonParser;

import java.lang.reflect.Type;
import java.util.List;

public class DataFactory {

    public static <T> List<T> getData(String filePath, Type typeOfT) {
        return GenericJsonParser.parseJson(filePath, typeOfT);
    }
}

