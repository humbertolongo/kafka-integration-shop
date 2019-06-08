package is.project3.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Path("/admin_manager")
public class Admin {


    @Path("/shipment_details")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getValueOrdersByProduct() {

        List<String> toSend = new ArrayList<>();

        Map<String, Integer> results1 = buildResults_Global(RESTServer.shipmentsStream, "QuantityOrderedByProductStore", Integer.class);
        Map<String, Double> results2 = buildResults_Global(RESTServer.shipmentsStream, "AverageQuantityByOrderStore", Double.class);
        //Map results2 = buildResults_Global(RESTServer.shipmentsStream, "NumOrdersByProductStore");


        try {
            return new ObjectMapper().writeValueAsString(results1);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
/*        results1.forEach((k,v)-> toSend.add("Product: "+k+
                " | No Orders: " + results2.get(k) +
                " | Total Ordered: " + results1.get(k) +
                " | Average Ordered: " + (Float.parseFloat((String) results1.get(k)) / Float.parseFloat((String) results2.get(k)))));


        return new JSON_Builder(toSend).getJson_String();*/


    }


    /*@Path("/items_sold_by_product_windowed")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getItemsSoldByProductWindowed(@QueryParam("minutes") int minutes){

        Map results = buildResults("ItemsSoldWindowStore", System.currentTimeMillis(), minutes*60*1000 );

        List<String> toSend = new ArrayList<>();
        results.forEach((k,v)-> toSend.add("Product: "+k+ " | Units Sold in the last "+minutes+" minutes: "+v));

        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/items_sold_by_product")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getItemsSoldByProduct(){

        List<String> toSend = new ArrayList<>();

        Map results1 = buildResults_Global(MyRESTServer.salesStream, "AmountSoldByProductStore");
        results1.forEach((k,v)-> toSend.add("Product: "+k+ " | Total Item Sold: "+v));

        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/max_price_sold_by_product")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMaxPriceSoldByProduct(){

        List<String> toSend = new ArrayList<>();

        Map results1 = buildResults_Global(MyRESTServer.salesStream, "MaxPriceByProductStore");
        results1.forEach((k,v)-> toSend.add("Product: "+k+ " | Max Price Sold: "+v));


        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/total_expenses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getOrders(){

        List<String> toSend = new ArrayList<>();

        Map results = buildResults_GlobalByKey_S1(MyRESTServer.shipmentsStream,"ExpensesStore", "Sum" );
        results.forEach((k,v)-> toSend.add("Total Expenses : "+v));

        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/total_revenue")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getRevenue(){

        List<String> toSend = new ArrayList<>();

        Map results = buildResults_GlobalByKey_S1(MyRESTServer.salesStream,"RevenueStore", "Revenue" );
        results.forEach((k,v)-> toSend.add("Total Revenue : "+v));

        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/total_profit")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getProfit(){

        List<String> toSend = new ArrayList<>();

        Map results1 = buildResults_GlobalByKey_S1(MyRESTServer.shipmentsStream,"ExpensesStore", "Sum" );
        Map results2 = buildResults_GlobalByKey_S1(MyRESTServer.salesStream,"RevenueStore", "Revenue" );

        toSend.add("Total Profit: " + (Integer.parseInt((String) results2.get("Revenue")) - Integer.parseInt((String)results1.get("Sum"))));

        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/total_items_sold")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getTotalItemsSold(){

        List<String> toSend = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();

        Map results1 = buildResults_Global(MyRESTServer.salesStream, "AmountSoldByProductStore");
        results1.forEach((k,v)-> count.set(count.get() + Integer.parseInt((String) v)));

        toSend.add("Total Item Sold: " + count.get());

        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/items_sold_windowed")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getItemsSoldWindowed(@QueryParam("minutes") int minutes) {


        Map results = buildResults("ItemsSoldWindowStore", System.currentTimeMillis(), minutes*60*1000 );

        List<String> toSend = new ArrayList<>();
        results.forEach((k,v)-> toSend.add("Product: "+k+ " | Units Sold in the last "+minutes+" minute(s): "+v));

        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/max_price_sold_windowed")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getMaxPriceSoldWindowed(@QueryParam("minutes") int minutes) {


        Map results = buildResults("MaxPriceSoldWindowStore", System.currentTimeMillis(), minutes*60*1000 );

        List<String> toSend = new ArrayList<>();
        results.forEach((k,v)-> toSend.add("Product: "+k+ " | Max Price Sold in the last "+minutes+" minute(s): "+v));

        return new JSON_Builder(toSend).getJson_String();
    }


    @Path("/highest_profit_by_item_windowed")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getHighestProfitByItemWindowed(@QueryParam("minutes") int minutes) {

        List<String> toSend = new ArrayList<>();
        Map<String,Float> aux = new HashMap();


        Map<String,String> results = buildResults("HighestProfitWindowStore", System.currentTimeMillis(), minutes*60*1000 );


        results.forEach((k,v) -> aux.put(k,Float.parseFloat((String) v)));

        aux.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEach(e -> {
                    //System.out.println("key: "+e.getKey());
                    //System.out.println("Value: "+e.getValue());
                    toSend.add("Product: "+e.getKey()+ " | Total Profit in the last "+minutes+" minute(s): "+ (e.getValue()*0.30));
                });

        return new JSON_Builder(toSend).getJson_String();

    }


    @Path("/average_price_sold_range")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAvgPriceSoldRange(@QueryParam("items") List<String> items) {

        List<String> toSend = new ArrayList<>();


        for (String item : items) {

            Map results = buildResults_GlobalByKey_S1(MyRESTServer.salesStream,"TotalPriceSalesByProduct", item );
            Map results2 = buildResults_GlobalByKey_S1(MyRESTServer.salesStream,"TotalCountSalesByProduct", item );

            results.forEach((k,v)-> toSend.add("Product: "+k+ " | Total Price Sold : "+v));
            results2.forEach((k,v)-> toSend.add("Product: "+k+ " | Total Count Sales : "+v));

            results.forEach((k,v) -> toSend.add("Product: "+k+" | Global Average Price: " + (Float.parseFloat((String) v)/Float.parseFloat((String) results2.get(k)))));

        }

        return new JSON_Builder(toSend).getJson_String();
    }*/


/*    private Map buildResults_Global (KafkaStreams ks, String storeName) {

        Map map = new HashMap();

        ReadOnlyKeyValueStore<String, String> store =
                ks.store(storeName, QueryableStoreTypes.keyValueStore());

        KeyValueIterator<String, String> iterator = store.all();
        while (iterator.hasNext()) {
            KeyValue<String, String> next = iterator.next();
            map.put(next.key, next.value);
        }

        return map;
    }*/

    private <T> Map<String, T> buildResults_Global(KafkaStreams ks, String storeName, Class<T> tClass) {

        Map<String, T> map = new HashMap<>();

        ReadOnlyKeyValueStore<String, T> store =
                ks.store(storeName, QueryableStoreTypes.keyValueStore());

        KeyValueIterator<String, T> iterator = store.all();
        while (iterator.hasNext()) {
            KeyValue<String, T> next = iterator.next();
            map.put(next.key, next.value);
        }

        return map;
    }

    /*private Map buildResults (String storeName, long timeTo, long timeFrom) {

        Map map = new HashMap();

        ReadOnlyWindowStore<String, String> windowStore =
                MyRESTServer.salesStream.store(storeName, QueryableStoreTypes.windowStore());

        KeyValueIterator<Windowed<String>, String> iterator2 = windowStore.fetchAll(timeTo-timeFrom, timeTo);
        while (iterator2.hasNext()) {
            KeyValue<Windowed<String>, String> next = iterator2.next();
            Windowed<String> windowTimestamp = next.key;
            map.put(windowTimestamp.key(),next.value);
        }

        return map;
    }

    private Map buildResults_GlobalByKey_S1 (KafkaStreams ks, String storeName, String key) {

        Map map = new HashMap();

        ReadOnlyKeyValueStore<String, String> store =
                ks.store(storeName, QueryableStoreTypes.keyValueStore());

        KeyValueIterator<String, String> iterator = store.all();
        while (iterator.hasNext()) {
            KeyValue<String, String> next = iterator.next();
            if (next.key.equals(key)){
                map.put(next.key, next.value);
            }
        }

        return map;
    }*/

}


