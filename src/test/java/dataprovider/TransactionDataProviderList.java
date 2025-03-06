package dataprovider;

import datamodel.Transaction;
import org.testng.annotations.DataProvider;
import utils.TransactionsWithoutWrapper;

import java.util.List;

public class TransactionDataProviderList {


        @DataProvider(name = "transactionsDataProvider")
        public Object[][] provideTransactions() {
            String filePath = "src/test/resources/buyProductsTestDataNoRoots.json"; // Path to your JSON
            List<Transaction> transactions = TransactionsWithoutWrapper.parseTransactions(filePath);

            // Convert List<Transaction> to Object[][]
            Object[][] data = new Object[transactions.size()][1];
            for (int i = 0; i < transactions.size(); i++) {
                data[i][0] = transactions.get(i);
            }
            return data;
        }
    }
