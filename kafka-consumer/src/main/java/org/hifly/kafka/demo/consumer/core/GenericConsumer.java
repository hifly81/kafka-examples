package org.hifly.kafka.demo.consumer.core;

import java.util.List;

public interface GenericConsumer {

    void subscribe(String groupId, String topic, boolean autoCommit);

    void poll(int size, long duration, boolean commitSync);

    boolean assign(String topic, List<Integer> partitions, boolean autoCommit);

    void shutdown();

}