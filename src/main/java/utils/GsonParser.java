package utils;

import com.google.gson.Gson;
import datamodel.UsersWrapper;

import java.io.FileReader;

public class GsonParser {

    public static UsersWrapper parseJson(String filePath) {
        UsersWrapper usersWrapper = null;

        try {
            Gson gson = new Gson();
            usersWrapper = gson.fromJson(new FileReader(filePath), UsersWrapper.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return usersWrapper;
    }
}

