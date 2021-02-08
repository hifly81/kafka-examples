package org.hifly.kafka.order.test;

import org.hifly.kafka.order.controller.OrderController;
import org.hifly.kafka.order.event.OrderEvent;
import org.hifly.kafka.order.model.Order;
import org.hifly.kafka.order.model.OrderItem;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderControllerTest {

    private KafkaSuiteTest server;
    private OrderController orderController;
    private Order order;

    @Before
    public void setUp() throws Exception {
        server = new KafkaSuiteTest();
        server.start();
        order = new Order();
        String orderId = "ID-" + UUID.randomUUID().toString();
        order.setId(orderId);
        order.setName(UUID.randomUUID().toString());

        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(UUID.randomUUID().toString());
        orderItem1.setName(UUID.randomUUID().toString());
        orderItem1.setPrice(100);
        orderItem1.setOrder(order);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setId(UUID.randomUUID().toString());
        orderItem2.setName(UUID.randomUUID().toString());
        orderItem2.setPrice(320);
        orderItem2.setOrder(order);

        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem1);
        items.add(orderItem2);
        order.setItems(items);
        orderController = new OrderController();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void create() {

        RecordMetadata recordMetadata = orderController.create(order);

        Assert.assertNotNull(recordMetadata);
        Assert.assertNotNull(recordMetadata.timestamp());
        Assert.assertNotNull(recordMetadata.partition());

    }

    @Test
    public void createOrderEvent() {

        OrderEvent orderEvent = orderController.createOrderEvent(order, OrderEvent.EventType.ORDER_ITEM_READY);

        Assert.assertNotNull(orderEvent);
        Assert.assertEquals(OrderEvent.EventType.ORDER_ITEM_READY, orderEvent.getEventType());
        Assert.assertEquals(2, orderEvent.getItemIds().size());
    }

    @Test
    public void ready() {
        OrderController.getOrders().put(order.getId(), order);
        RecordMetadata recordMetadata = orderController.ready(order.getId());

        Assert.assertNotNull(recordMetadata);
        Assert.assertNotNull(recordMetadata.timestamp());
        Assert.assertNotNull(recordMetadata.partition());
    }

    @Test
    public void itemReady() {
        OrderController.getOrders().put(order.getId(), order);
        RecordMetadata recordMetadata = orderController.itemReady(order.getItems().get(0));

        Assert.assertNotNull(recordMetadata);
        Assert.assertNotNull(recordMetadata.timestamp());
        Assert.assertNotNull(recordMetadata.partition());
    }

}