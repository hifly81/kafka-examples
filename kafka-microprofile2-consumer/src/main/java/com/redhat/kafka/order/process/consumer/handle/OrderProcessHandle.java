package com.redhat.kafka.order.process.consumer.handle;

import com.redhat.kafka.order.process.event.OrderEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderProcessHandle extends ConsumerHandle {

    private Logger log = LoggerFactory.getLogger(OrderProcessHandle.class);

    @Override
    public void process(ConsumerRecord record) {
        OrderEvent orderEvent = (OrderEvent) record.value();
        log.info("Received record from kafka {}", record.key());
        //ShipmentClient shipmentClient = new ShipmentClient();
        //shipmentClient.sendOrderEvent(REST_SHIPMENT_URI, orderEvent);
    }
}

