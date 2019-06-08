package is.project3.core.shop;

import is.project3.core.orders.CustomerOrder;
import is.project3.core.orders.MessageFactory;
import is.project3.core.utils.Utils;
import is.project3.data.Item;
import is.project3.data.ItemDAO;
import is.project3.data.PersistenceManager;
import is.project3.kafka.KafkaConsumerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static is.project3.core.TopicConfiguration.*;
import static is.project3.core.orders.Message.STATUS.*;
import static is.project3.core.orders.ShopResponse.*;


public class ShopConsumerFromPurchasesTopic implements Runnable {

    private List<String> l_Clients;

    ShopConsumerFromPurchasesTopic() {
        this.l_Clients = new ArrayList<>();
    }

    public void run() {

        Consumer<String, String> consumer = KafkaConsumerFactory.createConsumerSS(PURCHASES_GROUP);
        consumer.subscribe(Collections.singletonList(PURCHASES_TOPIC));

        System.out.println("Shop subscribed to topic " + PURCHASES_TOPIC);

        //noinspection InfiniteLoopStatement
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(CONSUMER_POOL_TIME));
            for (ConsumerRecord<String, String> record : records) {

                STATUS status;
                Optional<CustomerOrder> customerOrderOptional = Utils.readMessage(record, CustomerOrder.class);

                if (customerOrderOptional.isPresent()) {
                    CustomerOrder customerOrder = customerOrderOptional.get();

                    String clientID = customerOrder.getUserID();
                    if (!l_Clients.contains(clientID)) {
                        l_Clients.add(clientID);
                        System.out.println("New Client entered the Shop | ID: " + clientID);
                    } else {
                        System.out.println("A client returned to the Shop | ID: " + clientID);
                    }

                    System.out.println("Customer REQUEST:: " + customerOrder);

                    int price = customerOrder.getPrice();
                    int quantity = customerOrder.getQuantity();
                    String product = customerOrder.getProduct();

                    EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
                    ItemDAO itemDAO = new ItemDAO(em);
                    Optional<Item> optionalItem = itemDAO.getByName(product);


                    if (optionalItem.isPresent()) {
                        Item item = optionalItem.get();

                        if (item.getPrice() == price) {
                            if (item.getStock() >= quantity) {

                                int oldStock = item.getStock();
                                item.setStock(item.getStock() - quantity);
                                itemDAO.update(item);
                                System.out.println("Client " + clientID + " received " + product + " | Quantity: " + quantity + " | Unit_Price: " + price);
                                status = SUCCEDEED;

                                if ((oldStock - quantity) <= (oldStock * SHOP_REORDER_POLICY)) {
                                    System.out.println("Product: " + product + " | OldStock: " + oldStock + " | New Stock: " + (oldStock - quantity) + " :: Reordering");
                                    status = REQUESTED_REORDER;
                                }
                            } else {
                                System.out.println("The request is on Hold, waiting for stock delivery!");
                                status = ON_HOLD;
                            }

                        } else {
                            System.out.println("The price in the Client " + clientID + " offer was reject!");
                            System.out.println("Item Price: " + item.getPrice() + " | Price Offered: " + price);
                            status = REJECTED;
                        }
                    } else {
                        status = UNKNOWN_PRODUCT;
                    }

                    em.close();


                    if (status.equals(REQUESTED_REORDER) || status.equals(ON_HOLD)) {
                        ShopProducerToReorderTopic ShopPMRoT = new ShopProducerToReorderTopic(MessageFactory.createShopOrder(2 * quantity, product));
                        Thread t1 = new Thread(ShopPMRoT);
                        t1.run();
                    }

                    if (status.equals(REQUESTED_REORDER)) {
                        status = SUCCEDEED;
                    }
                    ShopProducerToReplyTopic ShopPMRT = new ShopProducerToReplyTopic(MessageFactory.createShopResponse(quantity, price, product, clientID, status));
                    Thread t1 = new Thread(ShopPMRT);
                    t1.run();
                }
            }
            consumer.commitAsync();
        }
    }
}
