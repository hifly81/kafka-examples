package org.hifly.kafka.demo.consumer.deserializer;

import java.util.List;

public interface IKafkaConsumer<K, V> {

    void subscribe(String groupId, String topic, boolean autoCommit);

    void poll(int size, long duration, boolean commitSync);

    boolean assign(String topic, List<Integer> partitions, boolean autoCommit);

    void shutdown();

}