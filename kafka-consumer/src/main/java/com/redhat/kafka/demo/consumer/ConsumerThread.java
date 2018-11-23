package com.redhat.kafka.demo.consumer;

import com.redhat.kafka.demo.consumer.handle.ConsumerHandle;

import java.util.Properties;

public class ConsumerThread<T> implements Runnable {

        private String id;
        private String groupId;
        private String topic;
        private String deserializerClass;
        private int size;
        private long duration;
        private boolean autoCommit;
        private boolean subscribeMode;
        private ConsumerHandle consumerHandle;

        public ConsumerThread(
                String id,
                String groupId,
                String topic,
                String deserializerClass,
                int pollSize,
                long duration,
                boolean autoCommit,
                boolean subscribeMode,
                ConsumerHandle consumerHandle) {
            this.id = id;
            this.groupId = groupId;
            this.topic = topic;
            this.deserializerClass = deserializerClass;
            this.size = pollSize;
            this.duration = duration;
            this.autoCommit = autoCommit;
            this.subscribeMode = subscribeMode;
            this.consumerHandle = consumerHandle;
        }

        public void run() {
            Properties properties = new Properties();
            properties.setProperty("desererializerClass", deserializerClass);
            BaseConsumer<T> consumer = new BaseConsumer<>(id, properties, consumerHandle);
            if(subscribeMode)
                consumer.subscribe(groupId, topic, autoCommit);
            else
                consumer.assign(topic, null, autoCommit);
            consumer.poll(size, duration);
        }

    }