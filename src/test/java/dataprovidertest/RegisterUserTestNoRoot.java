package dataprovidertest;

import datamodel.Address;
import datamodel.User;
import dataprovider.GenericDataProvider;
import dataprovider.UserDataProviderList;
import org.testng.annotations.Test;

public class RegisterUserTestNoRoot {

    @Test(dataProvider = "registerUsersDataProvider", dataProviderClass = UserDataProviderList.class)
    public void testRegisterUser(User user) {
        System.out.println("Registering User: " + user.getFirstName() + " " + user.getLastName());
        System.out.println("Address: " + user.getAddress().getStreet() + ", " + user.getAddress().getCity());
        System.out.println("Phone: " + user.getMobilePhone());
        // Add Selenium or assertion logic here
    }

    @Test(dataProvider = "registerUsersDataProvider", dataProviderClass = GenericDataProvider.class)
    public void RegisterUserGenericDataTest(User user) {
        System.out.println("Registering User: ");
        System.out.println("First Name: " + user.getFirstName());
        System.out.println("Last Name: " + user.getLastName());
        System.out.println("Date of Birth: " + user.getDateOfBirth());

        Address address = user.getAddress();
        System.out.println("Address: " + address.getNo() + ", Unit " + address.getUnit() +
                ", " + address.getStreet() + ", " + address.getSuburb() +
                ", " + address.getCity() + ", " + address.getPostCode() + ", " + address.getCountry());

        System.out.println("Home Phone: " + user.getHomePhone());
        System.out.println("Mobile Phone: " + user.getMobilePhone());

        // Add your Selenium logic for user registration here
    }
}

