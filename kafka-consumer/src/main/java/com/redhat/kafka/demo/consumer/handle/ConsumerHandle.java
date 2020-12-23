package com.redhat.kafka.demo.consumer.handle;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

public abstract class ConsumerHandle<K,V> {

    public abstract void addOffsets(Map<TopicPartition, OffsetAndMetadata> offsets);
    public abstract List<String> process(ConsumerRecords<K, V> consumerRecords);
}
