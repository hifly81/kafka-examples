package com.redhat.kafka.demo.consumer;

public interface BaseKafkaConsumer<K, V> {

    void subscribe(String groupId, String topic, boolean autoCommit);

    void poll(int size);
}