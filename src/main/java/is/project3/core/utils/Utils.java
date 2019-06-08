package is.project3.core.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import is.project3.core.orders.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.util.Optional;

public class Utils {

    public static <T extends Message> Optional<T> mapMessage(String messageString, Class<T> className) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            T message = objectMapper.readValue(messageString, className);
            return Optional.of(message);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static <T extends Message> Optional<T> readMessage(ConsumerRecord<String, String> record, Class<T> className) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            T message = objectMapper.readValue(record.value(), className);
            return Optional.of(message);
        } catch (IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }


    public static <T extends Message> String writeMessage(T object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
