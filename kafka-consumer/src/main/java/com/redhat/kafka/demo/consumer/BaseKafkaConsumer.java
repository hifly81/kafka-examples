package com.redhat.kafka.demo.consumer;

public interface BaseKafkaConsumer<K, V> {

    void subscribe(String groupId, String topic);

    void poll(int size);
}