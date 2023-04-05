package org.hifly.kafka.demo.consumer.core;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.hifly.kafka.demo.consumer.core.impl.GenericConsumerImpl;

import java.util.Properties;

public class ConsumerInstance<K, V>  {

        private String id;
        private String groupId;
        private String topic;
        private String keyDeserializerClass;
        private String valueDeserializerClass;
        private String partitionStrategy;
        private String isolationLevel;
        private int timeout;
        private long duration;
        private boolean autoCommit;
        private boolean commitSync;
        private boolean subscribeMode;
        private KafkaConsumer kafkaConsumer;
        private AbstractConsumerHandle consumerHandle;

        public ConsumerInstance(
                String id,
                String topic,
                KafkaConsumer kafkaConsumer,
                int timeout,
                long duration,
                boolean autoCommit,
                boolean commitSync,
                boolean subscribeMode,
                AbstractConsumerHandle consumerHandle
        ) {
            this.id = id;
            this.topic = topic;
            this.kafkaConsumer = kafkaConsumer;
            this.timeout = timeout;
            this.duration = duration;
            this.autoCommit = autoCommit;
            this.commitSync = commitSync;
            this.subscribeMode = subscribeMode;
            this.consumerHandle = consumerHandle;
        }

        public ConsumerInstance(
                String id,
                String groupId,
                String topic,
                String keyDeserializerClass,
                String valueDeserializerClass,
                String partitionStrategy,
                String isolationLevel,
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
            this.partitionStrategy = partitionStrategy;
            this.isolationLevel = isolationLevel;
            this.timeout = timeout;
            this.duration = duration;
            this.autoCommit = autoCommit;
            this.commitSync = commitSync;
            this.subscribeMode = subscribeMode;
            this.consumerHandle = consumerHandle;
        }

        public void consume() {
            Properties properties = null;
            if(kafkaConsumer == null) {
                properties = new Properties();
                properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializerClass);
                properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializerClass);
                properties.setProperty(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, partitionStrategy);
                properties.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel);
            }
            GenericConsumerImpl<K, V> consumer = new GenericConsumerImpl<>(kafkaConsumer, id, properties, consumerHandle);
            if(subscribeMode)
                consumer.subscribe(groupId, topic, autoCommit);
            else
                consumer.assign(topic, null, autoCommit);
            consumer.poll(timeout, duration, commitSync);
        }

    }