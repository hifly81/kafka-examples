package com.redhat.kafka.order.process.shipment;

import com.redhat.kafka.order.process.event.OrderEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RestClient {

    private static final String REST_SHIPMENT_URI =
            System.getenv("shipment.url") != null? System.getenv("shipment.url") :"http://localhost:8080/shipment";

    public void sendOrderEvent(OrderEvent orderEvent) {

        try {

            URL url = new URL(REST_SHIPMENT_URI);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            List<String> items = orderEvent.getItemIds();
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for(int i = 0; i < items.size(); i ++) {
                String tmp = items.get(i);
                sb.append("\""+tmp+"\"");
                if(i < items.size() -1)
                    sb.append(",");
            }
            sb.append("]");

            String input = "";

            if(orderEvent.getEventType() == OrderEvent.EventType.ORDER_ITEM_READY && orderEvent.getItemEvent() != null ) {
                String inputItem = "{\"id\":\""+orderEvent.getItemEvent().getId()+"\",\"name\":\""+orderEvent.getItemEvent().getName()+"\",\"orderId\":\""+orderEvent.getItemEvent().getOrderId()+"\",\"price\":"+orderEvent.getItemEvent().getPrice()+"}";
                input = "{\"eventType\":\""+orderEvent.getEventType()+"\",\"id\":\""+orderEvent.getId()+"\",\"name\":\""+orderEvent.getName()+"\",\"itemIds\":"+sb.toString()+",\"itemEvent\":"+inputItem+"}";
            } else {
                input = "{\"eventType\":\""+orderEvent.getEventType()+"\",\"id\":\""+orderEvent.getId()+"\",\"name\":\""+orderEvent.getName()+"\",\"itemIds\":"+sb.toString()+"}";

            }

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
