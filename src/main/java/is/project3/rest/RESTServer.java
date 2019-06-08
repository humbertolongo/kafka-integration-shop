package is.project3.rest;

import is.project3.core.orders.SupplierResponse;
import is.project3.core.utils.Utils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static is.project3.core.TopicConfiguration.REPLAY_TOPIC_GLOBAL;
import static is.project3.core.TopicConfiguration.SHIPMENTS_TOPIC;
import static is.project3.kafka.KafkaStreamFactory.createPropertiesStreamSS;

class RESTServer {

    private final static int port = 9998;
    private final static String host = "http://localhost/";

    public static KafkaStreams salesStream;
    public static KafkaStreams shipmentsStream;


    public static void main(String[] args) {
        URI baseUri = UriBuilder.fromUri(host).port(port).build();

        loadShipmentStreams();
        loadReplayStreams();


        ResourceConfig config = new ResourceConfig(Admin.class);
        JdkHttpServerFactory.createHttpServer(baseUri, config);
    }

    private static void loadShipmentStreams() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> shipmentsStream = builder.stream(SHIPMENTS_TOPIC);


        KStream<String, SupplierResponse> shipmentsTransformed = shipmentsStream.mapValues(message -> Utils.mapMessage(message, SupplierResponse.class).orElse(new SupplierResponse()));


        KTable<String, Integer> sumProduct = shipmentsTransformed.
                mapValues(SupplierResponse::getQuantity).
                groupByKey().
                reduce((v1, v2) -> v1 + v2,
                        Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as("QuantityOrderedByProductStore").withValueSerde(Serdes.Integer()));


        KTable<String, Integer> sumPrice = shipmentsTransformed.
                mapValues(response -> response.getPrice() * response.getQuantity()).
                groupByKey().
                reduce((v1, v2) -> v1 + v2,
                        Materialized.<String, Integer, KeyValueStore<Bytes, byte[]>>as("TotalPriceByProductStore").withValueSerde(Serdes.Integer()));


        KTable<String, Long> countOrders = shipmentsTransformed.
                groupByKey().
                count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("NumOrdersByProductStore").withValueSerde(Serdes.Long()));


        KTable<String, Double> avgOrdered = sumProduct.join(countOrders, (sum, count) -> sum.doubleValue() / count.doubleValue(),
                Materialized.<String, Double, KeyValueStore<Bytes, byte[]>>as("AverageQuantityByOrderStore").withValueSerde(Serdes.Double()));


        KTable<String, Double> avgPrice = sumPrice.join(countOrders, (sum, count) -> sum.doubleValue() / count.doubleValue(),
                Materialized.<String, Double, KeyValueStore<Bytes, byte[]>>as("AveragePriceByProductStore").withValueSerde(Serdes.Double()));



/*        shipmentsTransformed.
                mapValues(value -> String.valueOf(1)).
                groupByKey().
                reduce((v, v1) -> String.valueOf(Integer.parseInt(v)+Integer.parseInt(v1)),
                        Materialized.as("NumOrdersByProductStore"));


        shipmentsTransformed.
                map((key, value) -> KeyValue.pair("Sum", String.valueOf(Integer.parseInt((String) value.get(1))*Integer.parseInt((String) value.get(0))))).
                groupByKey().
                reduce((oldval, newval) -> String.valueOf(Integer.parseInt(oldval) + Integer.parseInt(newval)),
                        Materialized.as("ExpensesStore"));*/


        RESTServer.shipmentsStream = new KafkaStreams(builder.build(), createPropertiesStreamSS("ServerShipmentApp"));
        RESTServer.shipmentsStream.cleanUp();
        RESTServer.shipmentsStream.start();

        Runtime.getRuntime().addShutdownHook(new Thread(RESTServer.shipmentsStream::close));

    }

    private static void loadReplayStreams() {
        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> replayStream = builder.stream(REPLAY_TOPIC_GLOBAL);

        /*Gson gson = new Gson();

 Messages from Topic:
        key -> Status
        Value -> List [ product, quantity, price]



        // Filter only Success Sales Transform
        KStream<String, List> salesSuccess = replayStream.
                filter((k,v) -> k.equals("Success")).
                mapValues(v -> gson.fromJson(v, List.class));


        // Quantity Grouped by Product
        KGroupedStream<String, String> quantity_by_key = salesSuccess.
                map((k,v) -> KeyValue.pair((String) v.get(0), (String) v.get(1))).
                groupByKey();


        // Price Grouped by Product
        KGroupedStream<String, String> price_by_key = salesSuccess.
                map((k,v) -> KeyValue.pair((String) v.get(0), (String) v.get(2))).
                groupByKey();


        // Quantity * Price Global (Revenue)
        salesSuccess.
                map((k,v) -> KeyValue.pair("Revenue", String.valueOf(Integer.parseInt((String) v.get(2))* Integer.parseInt((String) v.get(1))))).
                groupByKey().
                reduce((v, v1) -> String.valueOf(Integer.parseInt((String) v)+Integer.parseInt(v1)),
                        Materialized.as("RevenueStore"));


        // Sum Quantity per Product
        quantity_by_key.reduce((v, v1) -> String.valueOf(Integer.parseInt((String) v)+Integer.parseInt(v1)),
                Materialized.as("AmountSoldByProductStore"));


        // Max Price per Product
        price_by_key.reduce((v, v1) -> String.valueOf(Math.max(Integer.parseInt(v),Integer.parseInt(v1))),
                Materialized.as("MaxPriceByProductStore"));




        // Max Price per Product Windowed
        KGroupedStream<String, String> max_price_sold_by_product_windowed = salesSuccess.
                map((k,v) -> KeyValue.pair((String) v.get(0), (String) v.get(2))).
                groupByKey();

        max_price_sold_by_product_windowed.windowedBy(TimeWindows.of(60000).advanceBy(1000)).
                reduce((v, v1) -> String.valueOf(Math.max(Integer.parseInt(v),Integer.parseInt(v1))),
                        Materialized.as("MaxPriceSoldWindowStore"));



        // Total Price Sales by Product
        KTable<String, String> total_sales_by_product = salesSuccess.
                map((k,v) -> KeyValue.pair((String) v.get(0), (String) v.get(2))).
                groupByKey().
                reduce((v, v1) -> String.valueOf(Integer.parseInt((String) v)+Integer.parseInt(v1)),
                        Materialized.as("TotalPriceSalesByProduct"));


        // Number Sales by Product
        KTable<String, String> number_sales_by_product = salesSuccess.
                map((k,v) -> KeyValue.pair((String) v.get(0), "1")).
                groupByKey().
                reduce((v, v1) -> String.valueOf(Integer.parseInt(v) + Integer.parseInt(v1)),
                        Materialized.as("TotalCountSalesByProduct"));



        // Highest profit per Product last x minute
        KGroupedStream<String, String> total_profit_last_x_minutes = salesSuccess.
                map((k,v) -> KeyValue.pair((String) v.get(0), String.valueOf(Integer.parseInt((String) v.get(2))* Integer.parseInt((String) v.get(1))))).
                groupByKey();

        total_profit_last_x_minutes.windowedBy(TimeWindows.of(60000).advanceBy(1000)).
                reduce((v, v1) -> String.valueOf(Math.max(Float.parseFloat(v),Float.parseFloat(v1))),
                        Materialized.as("HighestProfitWindowStore"));


        KGroupedStream<String, String> items_sold_windowed = salesSuccess.
                map((k,v) -> KeyValue.pair((String) v.get(0), String.valueOf(Integer.parseInt((String) v.get(1))))).
                groupByKey();

        items_sold_windowed.windowedBy(TimeWindows.of(60000).advanceBy(1000)).
                reduce((v, v1) -> String.valueOf(Integer.parseInt((String) v)+Integer.parseInt(v1)),
                        Materialized.as("ItemsSoldWindowStore"));

*/

        RESTServer.salesStream = new KafkaStreams(builder.build(), createPropertiesStreamSS("ServerReplayApp"));
        RESTServer.salesStream.cleanUp();
        RESTServer.salesStream.start();


        Runtime.getRuntime().addShutdownHook(new Thread(salesStream::close));

    }
}
