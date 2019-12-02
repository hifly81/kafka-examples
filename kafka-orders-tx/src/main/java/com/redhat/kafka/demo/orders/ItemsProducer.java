package com.redhat.kafka.demo.orders;

import com.redhat.kafka.demo.orders.controller.ItemController;
import com.redhat.kafka.demo.orders.model.Item;

import java.util.Arrays;

public class ItemsProducer {

    public static void main(String [] args) {

        Item item1 = new Item();
        item1.setId("111");
        item1.setCost(15.5f);
        item1.setDescription("Laptop Bag");
        //same order id
        item1.setOrderId("OD001");

        Item item2 = new Item();
        item2.setId("112");
        item2.setCost(25.8f);
        item2.setDescription("Gameboy");
        //same order id
        item2.setOrderId("OD001");

        ItemController itemController = new ItemController();
        itemController.sendItems(Arrays.asList(item1, item2), 5000);

    }
}
