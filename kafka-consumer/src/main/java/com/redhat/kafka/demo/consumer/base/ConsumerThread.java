package com.redhat.kafka.demo.consumer.base;

public class ConsumerThread implements Runnable {

        private String id;
        private String groupId;
        private String topic;
        private int size;
        private boolean autoCommit;
        private boolean subscribeMode;

        public ConsumerThread(
                String id,
                String groupId,
                String topic,
                int pollSize,
                boolean autoCommit,
                boolean subscribeMode) {
            this.id = id;
            this.groupId = groupId;
            this.topic = topic;
            this.size = pollSize;
            this.autoCommit = autoCommit;
            this.subscribeMode = subscribeMode;
        }

        public void run() {
            BaseConsumer consumer = new BaseConsumer(id);
            if(subscribeMode)
                consumer.subscribe(groupId, topic, autoCommit);
            else
                consumer.assign(topic, null, autoCommit);
            consumer.poll(size);
        }

    }