package is.project3.core.shop;

import is.project3.core.orders.ShopResponse;
import is.project3.core.utils.Utils;
import is.project3.data.Item;
import is.project3.data.ItemDAO;
import is.project3.data.PersistenceManager;
import is.project3.kafka.KafkaProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import javax.persistence.EntityManager;

import java.util.Optional;

import static is.project3.core.TopicConfiguration.*;
import static is.project3.core.orders.Message.STATUS.*;


public class ShopProducerToReplyTopic implements Runnable {
    private ShopResponse shopResponse;

    ShopProducerToReplyTopic(ShopResponse shopResponse) {
        this.shopResponse = shopResponse;
    }

    public void run() {

        String topicName = REPLAY_TOPIC_CUSTOMERS + shopResponse.getUserID();

        int price = shopResponse.getPrice();
        int quantity = shopResponse.getQuantity();
        String product = shopResponse.getProduct();
        String clientID = shopResponse.getUserID();

        while (shopResponse.getStatus().equals(ON_HOLD)) {

            EntityManager em = PersistenceManager.INSTANCE.getEntityManager();
            ItemDAO itemDAO = new ItemDAO(em);
            Optional<Item> optionalItem = itemDAO.getByName(product);

            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();

                if (item.getStock() >= quantity) {
                    item.setStock(item.getStock() - quantity);
                    itemDAO.update(item);
                    System.out.println("Client " + clientID + " received " + product + " | Quantity: " + quantity + " | Price: " + price);
                    shopResponse.setStatus(SUCCEDEED);
                } else {
                    System.out.printf("The request from Client %s is On Hold, waiting for stock delivery! :: " +
                            "Product: %s | Quantity: %s | Unit_Price: %s\n", clientID, product, quantity, price);
                    shopResponse.setStatus(ON_HOLD);
                }
            } else {
                shopResponse.setStatus(UNKNOWN_PRODUCT);
            }
            em.close();

            try {
                Thread.sleep(RETRY_TIME_SHOP_ON_HOLD);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String messageString = Utils.writeMessage(shopResponse);

        Producer<String, String> producer = KafkaProducerFactory.createProducerSS();
        producer.send(new ProducerRecord<>(topicName, messageString));
        producer.close();

        Producer<String, String> producer2 = KafkaProducerFactory.createProducerSS();
        producer2.send(new ProducerRecord<>(REPLAY_TOPIC_GLOBAL, messageString));
        producer2.close();
    }
}
