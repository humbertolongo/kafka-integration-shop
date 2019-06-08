package is.project3.core.owner;

import is.project3.core.orders.OwnerOrder;
import is.project3.core.utils.Utils;
import is.project3.kafka.KafkaProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import static is.project3.core.TopicConfiguration.REORDER_TOPIC;

public class OwnerProducerToReorderTopic implements Runnable {

    private OwnerOrder ownerOrder;

    OwnerProducerToReorderTopic(OwnerOrder ownerOrder) {
        this.ownerOrder = ownerOrder;
    }

    public void run() {
        Producer<String, String> producer = KafkaProducerFactory.createProducerSS();

        producer.send(new ProducerRecord<>(REORDER_TOPIC, "Owner", Utils.writeMessage(ownerOrder)));

        System.out.println("Owner REQUEST: " + ownerOrder);

        producer.close();

    }
}
