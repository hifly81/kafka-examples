package com.redhat.kafka.order;

import com.redhat.kafka.order.controller.OrderController;
import com.redhat.kafka.order.model.Order;
import com.redhat.kafka.order.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderApp {


    public static void main (String [] args) {
        OrderController orderController = new OrderController();


        for(int i=0; i<=500; i++) {
            Order order = new Order();
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

            orderController.create(order);

            for (OrderItem item : items)
                orderController.itemReady(item);

            orderController.ready(orderId);
        }



    }




}
