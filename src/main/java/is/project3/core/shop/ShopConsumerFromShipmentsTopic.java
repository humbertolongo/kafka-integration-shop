package is.project3.core.shop;

import is.project3.core.orders.SupplierResponse;
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
import java.util.Collections;
import java.util.Optional;

import static is.project3.core.TopicConfiguration.*;


public class ShopConsumerFromShipmentsTopic implements Runnable {

    ShopConsumerFromShipmentsTopic() {
    }

    public void run() {

        Consumer<String, String> consumer = KafkaConsumerFactory.createConsumerSS(SHIPMENTS_GROUP);
        consumer.subscribe(Collections.singletonList(SHIPMENTS_TOPIC));

        System.out.println("Shop Subscribed to topic " + SHIPMENTS_TOPIC);

        //noinspection InfiniteLoopStatement
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(CONSUMER_POOL_TIME));
            for (ConsumerRecord<String, String> record : records) {

                Optional<SupplierResponse> read = Utils.readMessage(record, SupplierResponse.class);

                if (read.isPresent()) {
                    SupplierResponse supplierResponse = read.get();

                    System.out.println("Supplier RESPONSE:: " + supplierResponse);

                    String product = supplierResponse.getProduct();
                    int unitPrice = supplierResponse.getUnitPrice();
                    int quantity = supplierResponse.getQuantity();


                    EntityManager em = PersistenceManager.INSTANCE.getEntityManager();

                    ItemDAO itemDAO = new ItemDAO(em);
                    Optional<Item> optionalItem = itemDAO.getByName(product);

                    if (optionalItem.isPresent()) {
                        Item item = optionalItem.get();
                        item.setPrice((int) (unitPrice * SHOP_MARGIN));
                        item.setStock(item.getStock() + quantity);
                        itemDAO.update(item);
                        System.out.printf("Stock Added :: Product %s | Quantity: %s | UnitPrice: %s | SalePrice: %s \n", product, quantity, unitPrice, unitPrice * SHOP_MARGIN);
                    } else {
                        Item i = new Item(product, quantity, (int) (unitPrice * SHOP_MARGIN));
                        itemDAO.save(i);
                        System.out.printf("New Product Added to the Shop! :: Product %s | Quantity: %s | UnitPrice: %s\n", product, quantity, unitPrice);
                    }
                    em.close();
                }

            }
            consumer.commitAsync();
        }
    }

}
