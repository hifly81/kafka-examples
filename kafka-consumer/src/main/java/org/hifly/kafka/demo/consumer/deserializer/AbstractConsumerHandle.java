package org.hifly.kafka.demo.consumer.deserializer;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

public abstract class AbstractConsumerHandle<K,V> {

    public abstract void addOffsets(Map<TopicPartition, OffsetAndMetadata> offsets);
    public abstract void process(ConsumerRecords<K, V> consumerRecords);
}
