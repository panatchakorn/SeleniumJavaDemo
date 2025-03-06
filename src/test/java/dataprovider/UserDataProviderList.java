package dataprovider;

import datamodel.User;
import org.testng.annotations.DataProvider;
import utils.RegisterUsersWithoutWrapper;

import java.util.List;

public class UserDataProviderList {

    @DataProvider(name = "registerUsersDataProvider")
    public Object[][] provideUsers() {
        String filePath = "src/test/resources/registerUserDataNoRoots.json"; // Path to your JSON
        List<User> users = RegisterUsersWithoutWrapper.parseUsers(filePath);

        // Convert List<User> to Object[][]
        Object[][] data = new Object[users.size()][1];
        for (int i = 0; i < users.size(); i++) {
            data[i][0] = users.get(i);
        }
        return data;
    }
}

