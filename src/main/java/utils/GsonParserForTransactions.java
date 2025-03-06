package utils;

import com.google.gson.Gson;
import datamodel.TransactionsWrapper;

import java.io.FileReader;

public class GsonParserForTransactions {

    public static TransactionsWrapper parseJson(String filePath) {
        TransactionsWrapper transactionsWrapper = null;

        try {
            Gson gson = new Gson();
            transactionsWrapper = gson.fromJson(new FileReader(filePath), TransactionsWrapper.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactionsWrapper;
    }
}
