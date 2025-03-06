package dataprovider;

import datamodel.TransactionsWrapper;
import org.testng.annotations.DataProvider;
import utils.GsonParserForTransactions;

public class TransactionsDataProvider {

    @DataProvider(name = "transactionsDataProvider")
    public Object[][] provideTransactionsData() {
        String filePath = "src/test/resources/buyProductsTestData.json"; // Update the path
        TransactionsWrapper transactionsWrapper = GsonParserForTransactions.parseJson(filePath);

        // Convert List<Transaction> to Object[][]
        Object[][] data = new Object[transactionsWrapper.getTransactions().size()][1];
        for (int i = 0; i < transactionsWrapper.getTransactions().size(); i++) {
            data[i][0] = transactionsWrapper.getTransactions().get(i);
        }

        return data;
    }
}

