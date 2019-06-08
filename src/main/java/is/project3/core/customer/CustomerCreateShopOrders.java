package is.project3.core.customer;

import is.project3.core.orders.CustomerOrder;
import is.project3.core.orders.MessageFactory;
import is.project3.data.Item;
import is.project3.data.ItemDAO;
import is.project3.data.PersistenceManager;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Random;

import static is.project3.core.TopicConfiguration.MAX_UNITS_CLIENT_AUT;
import static is.project3.core.TopicConfiguration.TIME_CUSTOMER_AUTO;

public class CustomerCreateShopOrders implements Runnable {

    private final String userID;

    CustomerCreateShopOrders(String userID) {
        this.userID = userID;
    }

    @Override
    public void run() {

        CustomerConsumerFromReplayTopic CustomerCRT = new CustomerConsumerFromReplayTopic(userID);
        Thread t2 = new Thread(CustomerCRT);
        t2.start();

        //noinspection InfiniteLoopStatement
        while (true) {

            EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
            ItemDAO itemDAO = new ItemDAO(em);
            List<Item> listProducts = itemDAO.getAll();
            em.close();

            Random rand = new Random();
            int productIndex = rand.nextInt((listProducts.size()));

            try {
                Item product = listProducts.get(productIndex);
                int productQuantity = rand.nextInt((MAX_UNITS_CLIENT_AUT) + 1);

                CustomerOrder customerOrder = MessageFactory.createCustomerOrder(productQuantity, product.getPrice(), product.getName(), userID);
                System.out.println("Customer REQUEST: " + customerOrder);

                CustomerProducerToPurchasesTopic CustomerPPT = new CustomerProducerToPurchasesTopic(customerOrder);
                Thread t1 = new Thread(CustomerPPT);
                t1.start();
            } catch (Exception e) {
                System.out.println("Something went wrong with the request in the Client!");
                e.printStackTrace();
            }

            try {
                Thread.sleep(TIME_CUSTOMER_AUTO);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
