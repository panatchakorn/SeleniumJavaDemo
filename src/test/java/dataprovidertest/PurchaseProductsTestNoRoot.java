package dataprovidertest;

import datamodel.Product;
import datamodel.Transaction;
import dataprovider.GenericDataProvider;
import dataprovider.TransactionDataProviderList;
import org.testng.annotations.Test;

public class PurchaseProductsTestNoRoot {

        @Test(dataProvider = "transactionsDataProvider", dataProviderClass = TransactionDataProviderList.class)
        public void testPurchaseTransaction(Transaction transaction) {
            System.out.println("Testing Purchase for User: " + transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName());

            for (Product product : transaction.getProducts()) {
                System.out.println("Product: " + product.getName() + " - Amount: " + product.getAmount());
            }

            System.out.println("Total Price: " + transaction.getTotalPrice());
            // Add Selenium or assertion logic here
        }

    @Test(dataProvider = "transactionsDataProvider", dataProviderClass = GenericDataProvider.class)
    public void testPurchaseTransactionGenericData(Transaction transaction) {
        System.out.println("Testing Purchase for User: " + transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName());

        for (Product product : transaction.getProducts()) {
            System.out.println("Product: " + product.getName() + " - Amount: " + product.getAmount());
        }

        System.out.println("Total Price: " + transaction.getTotalPrice());
        // Add Selenium or assertion logic here
    }
    }

