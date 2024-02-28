package org.hifly.kafka.demo.orders.controller;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hifly.kafka.demo.orders.kafka.KafkaConfig;
import org.hifly.kafka.demo.orders.kafka.producer.ItemJsonSerializer;
import org.hifly.kafka.demo.orders.kafka.producer.OrderJsonSerializer;
import org.hifly.kafka.demo.orders.model.Item;
import org.hifly.kafka.demo.orders.model.Order;
import org.hifly.kafka.demo.producer.serializer.json.JsonProducer;

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

        Properties props = KafkaConfig.jsonProducer(ItemJsonSerializer.class.getName(), "cart-app");
        KafkaProducer<String, Item> producer = new KafkaProducer<>(props);

        JsonProducer<Item> jsonProducer = new JsonProducer<>();
        jsonProducer.start(producer);

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

        Properties props = KafkaConfig.jsonProducer(OrderJsonSerializer.class.getName(), "cart-app");
        KafkaProducer<String, Order> producer = new KafkaProducer<>(props);

        JsonProducer<Order> jsonProducer = new JsonProducer<>();
        jsonProducer.start(producer);

        if(generateConsGroup)
            consGroup = UUID.randomUUID().toString();

        log.info("Consumer group - orders {}", consGroup);

        Properties consProperties = KafkaConfig.jsonConsumer(
                consGroup, "org.hifly.kafka.demo.orders.kafka.consumer.ItemJsonDeserializer");

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

