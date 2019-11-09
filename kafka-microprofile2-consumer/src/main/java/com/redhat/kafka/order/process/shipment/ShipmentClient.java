package com.redhat.kafka.order.process.shipment;

import com.redhat.kafka.order.process.event.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ShipmentClient {

    private Logger log = LoggerFactory.getLogger(ShipmentClient.class);

    public int sendOrderEvent(
            String shipmentUrl,
            OrderEvent orderEvent) {

        HttpURLConnection conn = null;

        try {

            URL url = new URL(shipmentUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            List<String> items = orderEvent.getItemIds();

            if(items == null || items.size() == 0)
                return HttpURLConnection.HTTP_NO_CONTENT;

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

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("Error in sendOrderEvent, error code {}", conn.getResponseCode());
                return conn.getResponseCode();
            }

            conn.disconnect();

            return HttpURLConnection.HTTP_OK;

        } catch (MalformedURLException e) {
            log.error("Error in sendOrderEvent!", e);
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        } catch (IOException e) {
            log.error("Error in sendOrderEvent!", e);
            return HttpURLConnection.HTTP_INTERNAL_ERROR;
        } finally {
            if(conn != null)
                conn.disconnect();
        }

    }


}
