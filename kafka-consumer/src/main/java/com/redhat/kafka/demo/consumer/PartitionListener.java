package com.redhat.kafka.demo.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.util.Collection;
import java.util.Map;

public class PartitionListener implements ConsumerRebalanceListener {

    private Consumer<String, String> consumer;
    private Map<TopicPartition, OffsetAndMetadata> offsets;

    public PartitionListener(Consumer<String, String> consumer, Map<TopicPartition, OffsetAndMetadata> offsets) {
        this.consumer = consumer;
        this.offsets = offsets;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {
        System.out.printf("rebalancing, for consumer: " + consumer + " - offsets:" + offsets);
        consumer.commitSync();
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> collection) {

    }
}
