package org.hifly.kafka.demo.consumer.core.impl;

import org.hifly.kafka.demo.consumer.core.AbstractConsumerHandle;
import org.hifly.kafka.demo.consumer.core.GenericConsumer;
import org.hifly.kafka.demo.consumer.core.KafkaConfig;
import org.hifly.kafka.demo.consumer.offset.OffsetManager;
import org.hifly.kafka.demo.consumer.partition.PartitionListener;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

public class GenericConsumerImpl<K, V> implements GenericConsumer {

    private Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private Consumer<K, V> consumer;
    private AbstractConsumerHandle consumerHandle;
    private Properties properties;
    private String id;
    private String groupId;
    private boolean autoCommit;
    private boolean keepConsuming = true;

    public GenericConsumerImpl(Consumer<K, V> consumer, String id, Properties properties, AbstractConsumerHandle consumerHandle) {
        this.consumer = consumer;
        this.id = id;
        this.properties = properties;
        this.consumerHandle = consumerHandle;
    }

    @Override
    public void shutdown() {
        keepConsuming = false;

    }

    @Override
    public void subscribe(String groupId, String topic, boolean autoCommit) {
        if (consumer == null)
            consumer = new KafkaConsumer<>(
                    KafkaConfig.consumerConfig(groupId, properties.getProperty(KEY_DESERIALIZER_CLASS_CONFIG), properties.getProperty(VALUE_DESERIALIZER_CLASS_CONFIG), autoCommit));
        consumer.subscribe(Collections.list(Collections.enumeration(Arrays.asList(topic.split(",")))), new PartitionListener(consumer, offsets));
        this.autoCommit = autoCommit;
        this.groupId = groupId;
    }

    @Override
    public boolean assign(String topic, List partitions, boolean autoCommit) {
        boolean isAssigned = false;
        consumer = new KafkaConsumer<>(
                KafkaConfig.consumerConfig(groupId, properties.getProperty(KEY_DESERIALIZER_CLASS_CONFIG), properties.getProperty(VALUE_DESERIALIZER_CLASS_CONFIG), autoCommit));
        List<PartitionInfo> partitionsInfo = consumer.partitionsFor(topic);
        Collection<TopicPartition> partitionCollection = new ArrayList<>();
        if (partitionsInfo != null) {
            for (PartitionInfo partition : partitionsInfo) {
                if (partitions == null || partitions.contains(partition.partition()))
                    partitionCollection.add(new TopicPartition(partition.topic(), partition.partition()));
            }
            if (!partitionCollection.isEmpty()) {
                consumer.assign(partitionCollection);
                isAssigned = true;
            }
        }
        this.autoCommit = autoCommit;
        return isAssigned;
    }

    @Override
    public void poll(int timeout, long duration, boolean commitSync) {
        if (consumer == null)
            throw new IllegalStateException("Can't poll, consumer not subscribed or null!");

        try {
            if (duration == -1) {
                while (keepConsuming)
                    consume(timeout, commitSync);
            } else {
                long startTime = System.currentTimeMillis();
                while (false || (System.currentTimeMillis() - startTime) < duration) {
                    consume(timeout, commitSync);
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            try {
                // print offsets
                // sync does retries, we want to use it in case of last commit or rebalancing
                consumer.commitSync();
                for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsets.entrySet())
                    System.out.printf("Consumer %s - partition %s - lastOffset %s\n", this.id,
                            entry.getKey().partition(), entry.getValue().offset());
                // Store offsets
                OffsetManager.store(offsets);
            } finally {
                consumer.close();
                System.out.printf("Bye Bye...\n");
            }
        }
    }

    private void consume(int timeout, boolean commitSync) {
        ConsumerRecords<K, V> records = consumer.poll(Duration.ofMillis(timeout));
        consumerHandle.addOffsets(offsets);
        consumerHandle.process(records, consumer.groupMetadata().groupId(), consumer.groupMetadata().memberId());

        if (!autoCommit)
            if (!commitSync) {
                try {
                    // async doesn't do a retry
                    consumer.commitAsync((map, e) -> {
                        if (e != null)
                            e.printStackTrace();
                    });
                } catch (CommitFailedException e) {
                    e.printStackTrace();
                }
            } else {
                consumer.commitSync();
            }
    }

}
