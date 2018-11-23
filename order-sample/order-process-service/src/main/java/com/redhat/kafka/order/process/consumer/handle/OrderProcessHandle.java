package com.redhat.kafka.order.process.consumer.handle;

import com.redhat.kafka.demo.consumer.handle.ConsumerHandle;
import com.redhat.kafka.order.process.event.OrderEvent;
import com.redhat.kafka.order.process.shipment.RestClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class OrderProcessHandle extends ConsumerHandle {

    @Override
    public void process(ConsumerRecord record) {
        OrderEvent orderEvent = (OrderEvent) record.value();
        RestClient restClient = new RestClient();
        restClient.sendOrderEvent(orderEvent);
    }
}
