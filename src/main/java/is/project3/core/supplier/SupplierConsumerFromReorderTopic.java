package is.project3.core.supplier;

import is.project3.core.orders.MessageFactory;
import is.project3.core.orders.ShopOrder;
import is.project3.core.orders.ShopResponse;
import is.project3.core.orders.SupplierResponse;
import is.project3.core.utils.Utils;
import is.project3.kafka.KafkaConsumerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import static is.project3.core.TopicConfiguration.*;

public class SupplierConsumerFromReorderTopic implements Runnable {

    SupplierConsumerFromReorderTopic() {
    }

    public void run() {

        Consumer<String, String> consumer = KafkaConsumerFactory.createConsumerSS(REORDER_GROUP);
        consumer.subscribe(Collections.singletonList(REORDER_TOPIC));

        System.out.println("Subscribed to topic " + REORDER_TOPIC);

        //noinspection InfiniteLoopStatement
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(CONSUMER_POOL_TIME));
            for (ConsumerRecord<String, String> record : records) {

                Optional<ShopOrder> read = Utils.readMessage(record, ShopOrder.class);

                if (read.isPresent()) {
                    ShopOrder shopOrder = read.get();

                    System.out.println("Shop REQUEST:: " + shopOrder);

                    String product = shopOrder.getProduct();
                    int quantity = shopOrder.getQuantity();

                    Random rand = new Random();
                    int price = rand.nextInt((MAX_PRICE_SUPPLIER - MIN_PRICE_SUPPLIER) + 1) + MIN_PRICE_SUPPLIER;


                    SupplierProducerToShipmentsTopic SupplierPST = new SupplierProducerToShipmentsTopic(MessageFactory.createSupplierResponse(product, quantity, price));
                    Thread t1 = new Thread(SupplierPST);
                    t1.start();
                }
            }
        }
    }
}
