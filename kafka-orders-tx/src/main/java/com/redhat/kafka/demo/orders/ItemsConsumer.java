package com.redhat.kafka.demo.orders;

import com.redhat.kafka.demo.orders.kafka.KafkaConfig;
import com.redhat.kafka.demo.orders.kafka.producer.JsonProducer;
import com.redhat.kafka.demo.orders.model.Item;
import com.redhat.kafka.demo.orders.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

import static java.time.Duration.ofSeconds;
import static java.util.Collections.singleton;

public class ItemsConsumer {

    public static void main(String[] args) throws Exception {

        final String TOPIC = "items";
        final String TOPIC_OUTPUT = "orders";
        final String CONS_GROUP = "group-id";

        Properties prodProperties = new Properties();
        prodProperties.put("valueSerializer", "com.redhat.kafka.demo.orders.kafka.producer.OrderJsonSerializer");
        prodProperties.put("txId", "order-app");

        JsonProducer<Order> jsonProducer = new JsonProducer<>();
        jsonProducer.start(prodProperties);

        Properties consProperties = KafkaConfig.jsonConsumer(
                CONS_GROUP, "com.redhat.kafka.demo.orders.kafka.consumer.ItemJsonDeserializer");

        KafkaConsumer<String, Item> jsonConsumer = new KafkaConsumer<>(consProperties);
        jsonConsumer.subscribe(singleton(TOPIC));

        jsonProducer.getProducer().initTransactions();

        try {

            while (true) {

                ConsumerRecords<String, Item> records = jsonConsumer.poll(ofSeconds(60));
                Order order = new Order();
                List<Item> items = new ArrayList<>(records.count());
                for (ConsumerRecord<String, Item> record : records) {
                    Item item = record.value();
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
            }
        } catch (Exception ex) {
            jsonProducer.getProducer().abortTransaction();
        }

        jsonProducer.stop();
    }
}
