package is.project3.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;


class RESTClient {
    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(args[0]);

        if (args[0].equals("http://localhost:9998/admin_manager/average_price_sold_range")) {
            for (int i = 1; i < args.length; i++) {
                webTarget = webTarget.queryParam("items", args[i]);
            }
        } else if (args.length > 1) {
            webTarget = webTarget.queryParam("minutes", args[1]);
        }

        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        @SuppressWarnings("unchecked")
        String responseJson = invocationBuilder.get(String.class);

        try {
            Map<String, String> results = new ObjectMapper().readValue(responseJson, new TypeReference<Map<String, String>>() {
            });
            results.forEach((k, v) -> System.out.println("Key: " + k + " Value: " + v));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("No results Found!");
        }

    }
}
