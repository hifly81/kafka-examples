package com.redhat.kafka.order.controller;

import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import com.redhat.kafka.demo.producer.serializer.json.JsonProducer;
import com.redhat.kafka.order.event.ItemEvent;
import com.redhat.kafka.order.event.OrderEvent;
import com.redhat.kafka.order.model.Order;
import com.redhat.kafka.order.model.OrderItem;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.*;
import java.util.stream.Collectors;

public class OrderController {

    private final String TOPIC = "orders";
    private static Properties properties;

    static {
        properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.order.producer.OrderEventJsonSerializer");
    }

    private static Map<String, Order> orders = new HashMap<>();

    public void create(Order order) {
        orders.put(order.getId(), order);
        OrderEvent orderEvent = createOrder(order, OrderEvent.EventType.ORDER_CREATED);
        JsonProducer<OrderEvent> jsonProducer = new JsonProducer<>();
        jsonProducer.start(properties);
        RecordMetadata lastRecord = jsonProducer.produceSync(new ProducerRecord<>(TOPIC, order.getId(), orderEvent));
        RecordMetadataUtil.prettyPrinter(lastRecord);
        jsonProducer.stop();

    }

    public void ready(String orderId) {
        Order order = orders.get(orderId);
        OrderEvent orderEvent = createOrder(order, OrderEvent.EventType.ORDER_READY);
        JsonProducer<OrderEvent> jsonProducer = new JsonProducer<>();
        jsonProducer.start(properties);
        RecordMetadata lastRecord = jsonProducer.produceSync(new ProducerRecord<>(TOPIC, orderId, orderEvent));
        RecordMetadataUtil.prettyPrinter(lastRecord);
        jsonProducer.stop();
    }

    public void itemReady(OrderItem orderItem) {
        Order order = orders.get(orderItem.getOrder().getId());
        OrderEvent orderEvent = createOrder(order, OrderEvent.EventType.ORDER_ITEM_READY);
        ItemEvent itemEvent = new ItemEvent();
        itemEvent.setId(orderItem.getId());
        itemEvent.setName(orderItem.getName());
        itemEvent.setOrderId(orderItem.getOrder().getId());
        itemEvent.setTimestamp(new Date());
        itemEvent.setPrice(orderItem.getPrice());
        orderEvent.setItemEvent(itemEvent);
        JsonProducer<OrderEvent> jsonProducer = new JsonProducer<>();
        jsonProducer.start(properties);
        RecordMetadata lastRecord = jsonProducer.produceSync(new ProducerRecord<>(TOPIC, orderItem.getOrder().getId(), orderEvent));
        RecordMetadataUtil.prettyPrinter(lastRecord);
        jsonProducer.stop();
    }

    private OrderEvent createOrder(
            Order order,
            OrderEvent.EventType eventType) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setId(order.getId());
        orderEvent.setTimestamp(new Date());
        orderEvent.setEventType(eventType);
        orderEvent.setName(order.getName());
        orderEvent.setItemIds(order.getItems().stream().map(i -> i.getId()).collect(Collectors.toList()));
        return orderEvent;
    }
}
