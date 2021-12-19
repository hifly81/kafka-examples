package org.hifly.kafka.demo.consumer.deserializer.impl;

import org.hifly.kafka.demo.consumer.deserializer.AbstractConsumerHandle;

import java.util.Properties;

public class ConsumerThread<K, V> implements Runnable {

        private String id;
        private String groupId;
        private String topic;
        private String keyDeserializerClass;
        private String valueDeserializerClass;
        private int timeout;
        private long duration;
        private boolean autoCommit;
        private boolean commitSync;
        private boolean subscribeMode;
        private AbstractConsumerHandle consumerHandle;

        public ConsumerThread(
                String id,
                String groupId,
                String topic,
                String keyDeserializerClass,
                String valueDeserializerClass,
                int timeout,
                long duration,
                boolean autoCommit,
                boolean commitSync,
                boolean subscribeMode,
                AbstractConsumerHandle consumerHandle) {
            this.id = id;
            this.groupId = groupId;
            this.topic = topic;
            this.keyDeserializerClass = keyDeserializerClass;
            this.valueDeserializerClass = valueDeserializerClass;
            this.timeout = timeout;
            this.duration = duration;
            this.autoCommit = autoCommit;
            this.commitSync = commitSync;
            this.subscribeMode = subscribeMode;
            this.consumerHandle = consumerHandle;
        }

        public void run() {
            Properties properties = new Properties();
            properties.setProperty("keyDeserializerClass", keyDeserializerClass);
            properties.setProperty("valueDeserializerClass", valueDeserializerClass);
            GenericConsumer<K, V> consumer = new GenericConsumer<>(null, id, properties, consumerHandle);
            if(subscribeMode)
                consumer.subscribe(groupId, topic, autoCommit);
            else
                consumer.assign(topic, null, autoCommit);
            consumer.poll(timeout, duration, commitSync);
        }

    }