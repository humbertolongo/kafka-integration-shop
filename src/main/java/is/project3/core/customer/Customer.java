package is.project3.core.customer;

import is.project3.core.orders.CustomerOrder;

public class Customer {

    public Customer() {
    }

    public static void main(String[] args) {

        if (args.length == 1) {
            // Creates an Customer that will create random orders from the Shop
            CustomerCreateShopOrders CustomerCSO = new CustomerCreateShopOrders(args[0]);
            Thread t3 = new Thread(CustomerCSO);
            t3.start();
        } else {
            // Creates a a specific order (passed as argument) and waits for the answer
            String customerId = args[0];
            String product = args[1];
            int quantity = Integer.parseInt(args[2]);
            int price = Integer.parseInt(args[3]);

            CustomerOrder customerOrder = new CustomerOrder(quantity, price, product, customerId);

            CustomerProducerToPurchasesTopic CustomerPPT = new CustomerProducerToPurchasesTopic(customerOrder);
            CustomerConsumerFromReplayTopic CustomerCRT = new CustomerConsumerFromReplayTopic(customerId);

            Thread t1 = new Thread(CustomerPPT);
            Thread t2 = new Thread(CustomerCRT);

            t1.start();
            t2.start();
        }
    }

}

