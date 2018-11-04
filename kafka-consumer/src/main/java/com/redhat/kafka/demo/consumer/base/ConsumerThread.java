package com.redhat.kafka.demo.consumer.base;

public class ConsumerThread implements Runnable {

        private String id;
        private String groupId;
        private String topic;
        private int size;

        public ConsumerThread(String id, String groupId, String topic, int size) {
            this.id = id;
            this.groupId = groupId;
            this.topic = topic;
            this.size = size;
        }

        public void run() {

            BaseConsumer consumer = new BaseConsumer(id);
            consumer.subscribe(groupId, topic);
            consumer.poll(size);

        }

    }