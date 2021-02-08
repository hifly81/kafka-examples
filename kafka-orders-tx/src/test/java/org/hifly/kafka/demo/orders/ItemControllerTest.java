package org.hifly.kafka.demo.orders;

import org.hifly.kafka.demo.orders.controller.ItemController;
import org.hifly.kafka.demo.orders.model.Item;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ItemControllerTest {

    private KafkaSuiteTest server;
    private ItemController itemController;
    private List<Item> items;

    @Before
    public void setUp() throws Exception {
        server = new KafkaSuiteTest();
        server.start();

        items = new ArrayList<>();

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

        items.add(item1);
        items.add(item2);

        itemController = new ItemController();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void sendItems() {

        List<RecordMetadata> result = itemController.sendItems(items, 5000);

        Assert.assertEquals(result.size(), 2);
        for(RecordMetadata recordMetadata: result) {
            Assert.assertNotNull(recordMetadata.timestamp());
            Assert.assertNotNull(recordMetadata.partition());
        }

    }

    @Test
    public void generateOrders() {
        List<RecordMetadata> result = itemController.sendItems(items, 5000);

        Assert.assertEquals(result.size(), 2);
        for(RecordMetadata recordMetadata: result) {
            Assert.assertNotNull(recordMetadata.timestamp());
            Assert.assertNotNull(recordMetadata.partition());
        }

        itemController.generateOrders(true, 5, true);
    }

}