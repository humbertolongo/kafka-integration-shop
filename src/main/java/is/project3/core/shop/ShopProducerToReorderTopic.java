package is.project3.core.shop;

import is.project3.core.orders.ShopOrder;
import is.project3.core.utils.Utils;
import is.project3.kafka.KafkaProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;


import static is.project3.core.TopicConfiguration.REORDER_TOPIC;

public class ShopProducerToReorderTopic implements Runnable {

    private ShopOrder shopOrder;

    ShopProducerToReorderTopic(ShopOrder shopOrder) {
        this.shopOrder = shopOrder;
    }

    public void run() {

        Producer<String, String> producer = KafkaProducerFactory.createProducerSS();

        producer.send(new ProducerRecord<>(REORDER_TOPIC, Utils.writeMessage(shopOrder)));
        System.out.println("Shop REQUEST: " + shopOrder);

        producer.close();
    }
}
