package org.hifly.kafka.demo.consumer.core.impl;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.hifly.kafka.demo.consumer.core.AbstractConsumerHandle;
import org.hifly.kafka.demo.consumer.core.GenericConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;


public class SharedConsumerImpl<K, V> implements GenericConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SharedConsumerImpl.class);

    private ShareConsumer<K, V> consumer;
    private final AbstractConsumerHandle<K, V> consumerHandle;
    private final Properties properties;
    private String groupId;
    private boolean keepConsuming = true;

    public SharedConsumerImpl(
            Properties properties,
            AbstractConsumerHandle<K, V> consumerHandle) {
        this.properties = properties;
        this.consumerHandle = consumerHandle;
    }

    @Override
    public void subscribe(String groupId, String topic, boolean autoCommit) {
        consumer = new KafkaShareConsumer<>(properties);
        consumer.subscribe(Collections.list(Collections.enumeration(Arrays.asList(topic.split(",")))));
        this.groupId = groupId;
    }

    @Override
    public void poll(Duration timeout, long duration, boolean commitSync) {
        if (consumer == null)
            throw new IllegalStateException("Can't poll, consumer not subscribed or null!");

        try {
            if (duration == -1) {
                while (keepConsuming)
                    consume(timeout);
            } else {
                long startTime = System.currentTimeMillis();
                while ((System.currentTimeMillis() - startTime) < duration) {
                    consume(timeout);
                }
            }
        } catch (WakeupException e) {
            // ignore for shutdown
        } finally {
            consumer.close();
            LOGGER.info("Bye Bye...\n");
        }
    }

    @Override
    public void assign(String topic, List<Integer> partitions, boolean autoCommit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void shutdown() {
        if (consumer == null)
            throw new IllegalStateException("Can't shutdown, consumer not subscribed or null!");
        keepConsuming = false;
    }

    private void consume(Duration timeout) {
        ConsumerRecords<K, V> records = consumer.poll(timeout);
        consumerHandle.process(records, groupId);
    }
}
