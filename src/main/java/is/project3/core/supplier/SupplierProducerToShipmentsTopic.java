package is.project3.core.supplier;

import is.project3.core.orders.SupplierResponse;
import is.project3.core.utils.Utils;
import is.project3.kafka.KafkaProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import static is.project3.core.TopicConfiguration.SHIPMENTS_TOPIC;

public class SupplierProducerToShipmentsTopic implements Runnable {

    private SupplierResponse supplierResponse;

    SupplierProducerToShipmentsTopic(SupplierResponse supplierResponse) {
        this.supplierResponse = supplierResponse;
    }

    public void run() {
        String supplierString = Utils.writeMessage(this.supplierResponse);
        System.out.println("Supplier RESPONSE : " + supplierResponse);

        Producer<String, String> producer = KafkaProducerFactory.createProducerSS();
        producer.send(new ProducerRecord<>(SHIPMENTS_TOPIC, supplierResponse.getProduct(), supplierString));
        producer.close();
    }
}
