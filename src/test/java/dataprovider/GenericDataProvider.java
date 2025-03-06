package dataprovider;

import com.google.gson.reflect.TypeToken;
import datamodel.DataFactory;
import datamodel.Transaction;
import datamodel.User;
import org.testng.annotations.DataProvider;
import utils.GenericJsonParser;

import java.lang.reflect.Type;
import java.util.List;

public class GenericDataProvider {

    @DataProvider(name = "genericDataProvider")
    public static Object[][] provideData(String filePath, Type typeOfT) {
        List<?> dataList = GenericJsonParser.parseJson(filePath, typeOfT);

        // Convert List<?> to Object[][]
        Object[][] data = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i);
        }
        return data;
    }

    @DataProvider(name = "registerUsersDataProvider")
    public Object[][] registerUsersData() {
        Type userListType = new TypeToken<List<User>>() {}.getType();
//        return GenericDataProvider.provideData("src/test/resources/registerUsers.json", userListType);
        return GenericDataProvider.provideData("src/test/resources/registerUserDataNoRoots.json", userListType);
    }

    @DataProvider(name = "transactionsDataProvider")
    public Object[][] transactionsData() {
        Type transactionListType = new TypeToken<List<Transaction>>() {}.getType();
//        return GenericDataProvider.provideData("src/test/resources/transactions.json", transactionListType);
        return GenericDataProvider.provideData("src/test/resources/buyProductsTestDataNoRoots.json", transactionListType);
    }

    @DataProvider(name = "dynamicDataProvider")
    public Object[][] dynamicDataProvider(String filePath, Type typeOfT) {
        List<?> dataList = DataFactory.getData(filePath, typeOfT);

        Object[][] data = new Object[dataList.size()][1];
        for (int i = 0; i < dataList.size(); i++) {
            data[i][0] = dataList.get(i);
        }
        return data;
    }


}

