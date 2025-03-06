package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;

public class GenericJsonParser {

    public static <T> T parseJson(String filePath, Type typeOfT) {
        T data = null;
        try {
            Gson gson = new Gson();
            data = gson.fromJson(new FileReader(filePath), typeOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    /* usage
    import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

Type userListType = new TypeToken<List<User>>() {}.getType();
List<User> users = GenericJsonParser.parseJson("src/test/resources/users.json", userListType);

Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();
List<Transaction> transactions = GenericJsonParser.parseJson("src/test/resources/transactions.json", transactionListType);


*/

}

