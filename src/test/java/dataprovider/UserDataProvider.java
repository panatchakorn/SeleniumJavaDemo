package dataprovider;

import datamodel.UsersWrapper;
import org.testng.annotations.DataProvider;
import utils.GsonParser;

public class UserDataProvider {

    @DataProvider(name = "registerUserDataProvider")
    public Object[][] provideRegisterUserData() {
        String filePath = "src/test/resources/registerUserData.json"; // Update the path to your JSON file
        UsersWrapper usersWrapper = GsonParser.parseJson(filePath);

        // Convert List<User> to Object[][]
        Object[][] data = new Object[usersWrapper.getUsers().size()][1];
        for (int i = 0; i < usersWrapper.getUsers().size(); i++) {
            data[i][0] = usersWrapper.getUsers().get(i);
        }

        return data;
    }
}

