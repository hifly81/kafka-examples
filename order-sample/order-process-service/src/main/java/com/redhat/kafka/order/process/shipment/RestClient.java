package com.redhat.kafka.order.process.shipment;

import com.redhat.kafka.order.process.event.OrderEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestClient {

    private static final String REST_URI = "http://localhost:8080/process-store";

    public void sendOrderEvent(OrderEvent orderEvent) {

        try {

            URL url = new URL(REST_URI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            String input = "{\"eventType\":\""+orderEvent.getEventType()+"\",\"id\":\""+orderEvent.getId()+"\",\"orderName\":\""+orderEvent.getOrderName()+"\"}";

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null)
                System.out.printf(output+"\n");

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
