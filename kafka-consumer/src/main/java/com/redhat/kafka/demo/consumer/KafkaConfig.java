package com.redhat.kafka.demo.consumer;

import java.util.Properties;

public class KafkaConfig {

    private static final String BROKER_LIST = "localhost:9092,localhost:9093,localhost:9094";

    public static Properties stringConsumer(String groupId) {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("group.id", groupId);
        producerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        producerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return producerProperties;
    }


}
