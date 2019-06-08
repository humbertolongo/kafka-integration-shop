package is.project3.core;

public interface TopicConfiguration {
    String REORDER_GROUP = "ReorderGroup";
    String SHIPMENTS_GROUP = "ReorderGroup";
    String REPLAY_GROUP = "ReplayGroup";
    String PURCHASES_GROUP = "PurchasesGroup";


    String REORDER_TOPIC = "ReorderTopic";
    String SHIPMENTS_TOPIC = "ShipmentsTopic";
    String PURCHASES_TOPIC = "PurchasesTopic";
    String REPLAY_TOPIC_CUSTOMERS = "ReplayTopic_";
    String REPLAY_TOPIC_GLOBAL = "ReplayTopicGlobal";


    int RETRY_TIME_SHOP_ON_HOLD = 20000; // 20 Seconds
    int CONSUMER_POOL_TIME = 100; // Miliseconds


    int MIN_PRICE_SUPPLIER = 5;
    int MAX_PRICE_SUPPLIER = 20;


    int TIME_CUSTOMER_AUTO = 10000; // 10 Seconds
    int MAX_UNITS_CLIENT_AUT = 20;


    double SHOP_MARGIN = 1.30;
    double SHOP_REORDER_POLICY = 0.25;


}
