package com.redhat.kafka.shipment.consumer.handle;

import com.redhat.kafka.demo.consumer.handle.ConsumerHandle;
import com.redhat.kafka.shipment.event.OrderEvent;
import com.redhat.kafka.shipment.store.InMemoryStore;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShipmentHandle extends ConsumerHandle {

    @Override
    public void process(ConsumerRecord record) {
        Map<String, List> store = InMemoryStore.getStore();
        OrderEvent orderEvent = (OrderEvent) record.value();
        if(store.containsKey(orderEvent.getId()))
            store.get(orderEvent.getId()).add(orderEvent);
        else {
            List<OrderEvent> orderEvents = new ArrayList<>();
            orderEvents.add(orderEvent);
            store.put(orderEvent.getId(), orderEvents);
        }
    }
}
