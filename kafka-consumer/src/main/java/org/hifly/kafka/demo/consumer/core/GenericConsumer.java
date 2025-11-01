package org.hifly.kafka.demo.consumer.core;

import java.time.Duration;
import java.util.List;

public interface GenericConsumer {

    void subscribe(String groupId, String topic, boolean autoCommit);

    void poll(Duration timeout, long duration, boolean commitSync);

    void assign(String topic, List<Integer> partitions, boolean autoCommit);

    void shutdown();

}