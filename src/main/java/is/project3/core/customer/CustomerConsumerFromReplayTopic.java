package is.project3.core.customer;

import is.project3.core.orders.ShopResponse;
import is.project3.core.utils.Utils;
import is.project3.kafka.KafkaConsumerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

import static is.project3.core.TopicConfiguration.*;

public class CustomerConsumerFromReplayTopic implements Runnable {
    private final String userID;

    CustomerConsumerFromReplayTopic(String userID) {
        this.userID = userID;
    }

    public void run() {

        String topicName = REPLAY_TOPIC_CUSTOMERS + userID;

        Consumer<String, String> consumer = KafkaConsumerFactory.createConsumerSS(REPLAY_GROUP + userID);
        consumer.subscribe(Collections.singletonList(topicName));

        System.out.println("Customer: " + userID + " Subscribed to topic " + topicName);

        //noinspection InfiniteLoopStatement
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(CONSUMER_POOL_TIME));
            for (ConsumerRecord<String, String> record : records) {

                Optional<ShopResponse> message = Utils.readMessage(record, ShopResponse.class);

                if (message.isPresent()) {
                    ShopResponse shopResponse = message.get();
                    System.out.println("Shop RESPONSE:: " + shopResponse);
                }
            }
            consumer.commitAsync();
        }
    }
}
