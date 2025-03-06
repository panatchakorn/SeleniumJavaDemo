package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import datamodel.Transaction;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class TransactionsWithoutWrapper {

    public static List<Transaction> parseTransactions(String filePath) {
        List<Transaction> transactions = null;
        try {
            Gson gson = new Gson();
            Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();
            transactions = gson.fromJson(new FileReader(filePath), transactionListType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }
}

