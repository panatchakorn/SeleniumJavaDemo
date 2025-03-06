package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import datamodel.User;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class RegisterUsersWithoutWrapper {

    public static List<User> parseUsers(String filePath) {
        List<User> users = null;
        try {
            Gson gson = new Gson();
            Type userListType = new TypeToken<List<User>>() {}.getType();
            users = gson.fromJson(new FileReader(filePath), userListType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
}
