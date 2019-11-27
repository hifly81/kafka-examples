package com.redhat.kafka.demo.orders;

import com.redhat.kafka.demo.orders.kafka.producer.JsonProducer;
import com.redhat.kafka.demo.orders.model.Item;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class ItemsProducer {

    public static void main(String [] args) throws Exception {

        final String TOPIC = "items";

        Properties properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.demo.orders.kafka.producer.ItemJsonSerializer");
        properties.put("txId", "cart-app");

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

        JsonProducer<Item> jsonProducer = new JsonProducer<>();
        jsonProducer.start(properties);

        jsonProducer.getProducer().initTransactions();

        try {
            jsonProducer.getProducer().beginTransaction();
            jsonProducer.produceSync(new ProducerRecord<>(TOPIC, item1.getOrderId(), item1));
            Thread.sleep(5000);
            jsonProducer.produceSync(new ProducerRecord<>(TOPIC, item1.getOrderId(), item2));
            jsonProducer.getProducer().commitTransaction();
        } catch(Exception ex) {
            jsonProducer.getProducer().abortTransaction();
        }

        jsonProducer.stop();
    }
}
