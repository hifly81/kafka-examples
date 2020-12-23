package com.redhat.kafka.demo.consumer;

import java.util.List;

public interface BaseKafkaConsumer<K, V> {

    void subscribe(String groupId, String topic, boolean autoCommit);

    void poll(int size, long duration, boolean commitSync);

    boolean assign(String topic, List<Integer> partitions, boolean autoCommit);

    public void shutdown();

}