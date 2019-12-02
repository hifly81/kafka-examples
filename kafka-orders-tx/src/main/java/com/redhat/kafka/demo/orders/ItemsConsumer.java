package com.redhat.kafka.demo.orders;

import com.redhat.kafka.demo.orders.controller.ItemController;

public class ItemsConsumer {

    public static void main(String[] args) {

        ItemController itemController = new ItemController();
        itemController.generateOrders(false, 60, false);
    }
}
