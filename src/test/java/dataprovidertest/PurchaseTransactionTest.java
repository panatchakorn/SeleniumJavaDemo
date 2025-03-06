package dataprovidertest;

import datamodel.Product;
import datamodel.Transaction;
import dataprovider.TransactionsDataProvider;
import org.testng.annotations.Test;

public class PurchaseTransactionTest {

    @Test(dataProvider = "transactionsDataProvider", dataProviderClass = TransactionsDataProvider.class)
    public void testPurchaseTransaction(Transaction transaction) {
        System.out.println("Testing Purchase for User: " + transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName());

        // Validate products
        for (Product product : transaction.getProducts()) {
            System.out.println("Product Code: " + product.getCode());
            System.out.println("Name: " + product.getName());
            System.out.println("Description: " + product.getDescription());
            System.out.println("Amount: " + product.getAmount());
        }

        // Validate total price
        System.out.println("Total Price: " + transaction.getTotalPrice());
        System.out.println("----------------------------------");

        // Add Selenium or assertion logic here
        // Example:
        // Assert.assertEquals(actualTotalPrice, transaction.getTotalPrice(), "Total price mismatch!");
    }
}
