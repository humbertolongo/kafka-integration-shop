package is.project3.kafka;

interface IKafkaConstants {
    String KAFKA_BROKERS = "127.0.0.1:9092";
    Integer MAX_POLL_RECORDS = 1;
    String ENABLE_AUTO_COMMIT = "true";
    Integer SESSION_TIMEOUT_MS = 30000;

    String ACKS = "all";
    Integer RETRIES = 5;
    Integer BATCH_SIZE = 16384;
    Integer LINGER_MS = 1;
    Integer BUFFER_MEMORY = 33554432;

    String APPLICATION_ID = "REST_APP";
}
