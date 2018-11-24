package com.redhat.kafka.order.process;

import com.redhat.kafka.order.process.controller.OrderProcessController;

public class OrderProcessApp {

    public static void main (String [] args) {
        OrderProcessController orderProcessController = new OrderProcessController();
        orderProcessController.receiveOrders(3, "group-1", -1, 10);
    }

}
