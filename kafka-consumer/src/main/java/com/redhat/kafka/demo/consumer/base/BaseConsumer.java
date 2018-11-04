package com.redhat.kafka.demo.consumer.base;

import com.redhat.kafka.demo.consumer.BaseKafkaConsumer;
import com.redhat.kafka.demo.consumer.ConsumerRecordUtil;
import com.redhat.kafka.demo.consumer.KafkaConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;

public class BaseConsumer implements BaseKafkaConsumer {

    private Consumer<String, String> consumer;
    private String id;

    public BaseConsumer(String id) {
        this.id = id;
    }

    @Override
    public void subscribe(String groupId, String topic) {
        consumer = new KafkaConsumer<>(KafkaConfig.stringConsumer(groupId));
        consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public void poll(int size) {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(size);
                for (ConsumerRecord<String, String> record : records) {
                    ConsumerRecordUtil.prettyPrinter(id, record);
                }
            }
        } finally {
            consumer.close();
        }

    }
}
