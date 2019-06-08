package is.project3.core.customer;

import is.project3.core.orders.CustomerOrder;
import is.project3.core.utils.Utils;
import is.project3.kafka.KafkaProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import static is.project3.core.TopicConfiguration.PURCHASES_TOPIC;

public class CustomerProducerToPurchasesTopic implements Runnable {

    private CustomerOrder customerOrder;

    CustomerProducerToPurchasesTopic(CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

    public void run() {

        Producer<String, String> producer = KafkaProducerFactory.createProducerSS();

        String orderString = Utils.writeMessage(this.customerOrder);

        producer.send(new ProducerRecord<>(PURCHASES_TOPIC, customerOrder.getUserID(), orderString));
        producer.close();

    }
}
