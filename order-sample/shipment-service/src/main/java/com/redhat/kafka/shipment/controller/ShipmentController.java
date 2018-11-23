package com.redhat.kafka.shipment.controller;

import com.redhat.kafka.demo.consumer.ConsumerThread;
import com.redhat.kafka.shipment.consumer.handle.ShipmentHandle;
import com.redhat.kafka.shipment.event.OrderEvent;
import com.redhat.kafka.shipment.store.InMemoryStore;

import java.util.List;
import java.util.Map;

public class ShipmentController {

    private final String TOPIC = "orders";

    public void receiveOrders(int numberOfConsumer, int duration, int pollSize) {
        for(int i = 0; i < numberOfConsumer; i++) {
            Thread t = new Thread(
                    new ConsumerThread<OrderEvent>(
                            String.valueOf(i),
                            "group-user-1",
                            TOPIC,
                            "com.redhat.kafka.shipment.consumer.OrderEventJsonDeserializer",
                            pollSize,
                            duration,
                            true ,
                            true,
                            new ShipmentHandle()));
            t.start();
        }
    }

    public void verifyOrders() {
        Runnable runnable = () -> {
            while(true) {
                System.out.printf("Entries in MAP:\n");
                Map<String, List> store = InMemoryStore.getStore();
                for (Map.Entry<String, List> entry : store.entrySet()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if(entry.getValue() != null && !entry.getValue().isEmpty()) {
                        for(Object obj: entry.getValue()) {
                            OrderEvent orderEvent = (OrderEvent)obj;
                            stringBuilder.append(orderEvent.getId() + "-" + orderEvent.getEventType() + "/");
                        }
                        System.out.printf(entry.getKey() + "/" + entry.getValue().size() + "/" + stringBuilder.toString() + "\n");
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread t = new Thread(runnable);
        t.start();
    }
}
