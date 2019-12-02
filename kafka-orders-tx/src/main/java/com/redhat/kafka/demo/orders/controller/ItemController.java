package com.redhat.kafka.demo.orders.controller;

import com.redhat.kafka.demo.orders.kafka.KafkaConfig;
import com.redhat.kafka.demo.orders.kafka.producer.JsonProducer;
import com.redhat.kafka.demo.orders.model.Item;
import com.redhat.kafka.demo.orders.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;

public class ItemController {

    private Logger log = LoggerFactory.getLogger(ItemController.class);

    public List<RecordMetadata> sendItems(List<Item> items, int delay) {

        List<RecordMetadata> result = new ArrayList<>();

        final String TOPIC = "items";

        Properties properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.demo.orders.kafka.producer.ItemJsonSerializer");
        properties.put("txId", "cart-app");

        JsonProducer<Item> jsonProducer = new JsonProducer<>();
        jsonProducer.start(properties);

        jsonProducer.getProducer().initTransactions();

        try {
            jsonProducer.getProducer().beginTransaction();
            for (Item item : items) {
                RecordMetadata recordMetadata = jsonProducer.produceSync(new ProducerRecord<>(TOPIC, item.getOrderId(), item));
                result.add(recordMetadata);
                Thread.sleep(delay);
            }
            jsonProducer.getProducer().commitTransaction();
        } catch (Exception ex) {
            jsonProducer.getProducer().abortTransaction();
        }

        jsonProducer.stop();

        return result;
    }

    public void generateOrders(
            boolean generateConsGroup,
            int pollSeconds,
            boolean singlePoll) {
        final String TOPIC = "items";
        final String TOPIC_OUTPUT = "orders";
        final String CONS_GROUP = "group-id";

        String consGroup = CONS_GROUP;

        Properties prodProperties = new Properties();
        prodProperties.put("valueSerializer", "com.redhat.kafka.demo.orders.kafka.producer.OrderJsonSerializer");
        prodProperties.put("txId", "order-app");

        JsonProducer<Order> jsonProducer = new JsonProducer<>();
        jsonProducer.start(prodProperties);

        if(generateConsGroup)
            consGroup = UUID.randomUUID().toString();

        log.info("Consumer group - orders {}", consGroup);

        Properties consProperties = KafkaConfig.jsonConsumer(
                consGroup, "com.redhat.kafka.demo.orders.kafka.consumer.ItemJsonDeserializer");

        KafkaConsumer<String, Item> jsonConsumer = new KafkaConsumer<>(consProperties);
        jsonConsumer.subscribe(singleton(TOPIC));

        jsonProducer.getProducer().initTransactions();

        try {

            if(!singlePoll) {
                while (true) {
                    int recordsCount = consumeItems(pollSeconds, TOPIC_OUTPUT, consGroup, jsonProducer, jsonConsumer, false);
                    if (recordsCount == 0)
                        continue;
                }
            } else
                //singlePoll always from beginning
                consumeItems(pollSeconds, TOPIC_OUTPUT, CONS_GROUP, jsonProducer, jsonConsumer, true);
        } catch (Exception ex) {
            jsonProducer.getProducer().abortTransaction();
        }

        jsonProducer.stop();
    }

    private int consumeItems(int pollSeconds, String TOPIC_OUTPUT, String CONS_GROUP, JsonProducer<Order> jsonProducer, KafkaConsumer<String, Item> jsonConsumer, boolean fromBeginning) {
        log.info("Fetching items...");
        ConsumerRecords<String, Item> records;

        if(fromBeginning) {
            records = jsonConsumer.poll(ofSeconds(0));
            jsonConsumer.seekToBeginning(records.partitions());
        }

        records = jsonConsumer.poll(ofSeconds(pollSeconds));

        Order order = new Order();
        List<Item> items = new ArrayList<>(records.count());
        for (ConsumerRecord<String, Item> record : records) {
            Item item = record.value();
            log.info("Received item {}", item.getId());
            order.setId(item.getOrderId());
            items.add(item);
        }
        order.setItems(items);

        jsonProducer.getProducer().beginTransaction();
        jsonProducer.produceSync(new ProducerRecord<>(TOPIC_OUTPUT, order.getId(), order));

        Map<TopicPartition, OffsetAndMetadata> offsetsToCommit = new HashMap<>();

        for (TopicPartition partition : records.partitions()) {
            List<ConsumerRecord<String, Item>> partitionedRecords = records.records(partition);
            long offset = partitionedRecords.get(partitionedRecords.size() - 1).offset();

            offsetsToCommit.put(partition, new OffsetAndMetadata(offset + 1));
        }

        jsonProducer.getProducer().sendOffsetsToTransaction(offsetsToCommit, CONS_GROUP);
        jsonProducer.getProducer().commitTransaction();

        return records.count();
    }
}

