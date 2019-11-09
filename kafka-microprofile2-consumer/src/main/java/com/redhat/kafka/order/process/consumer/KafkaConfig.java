package com.redhat.kafka.order.process.consumer;

import java.util.Properties;

public class KafkaConfig {

    private static final String BROKER_LIST =
            System.getenv("KAFKABROKERLIST") != null? System.getenv("KAFKABROKERLIST") :"localhost:9092,localhost:9093,localhost:9094";


    public static Properties baseConsumerConfig(String groupId, String valueSerializerClassName, boolean autoCommit) {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("group.id", groupId);
        producerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        producerProperties.put("value.deserializer", valueSerializerClassName);
        producerProperties.setProperty("enable.auto.commit", String.valueOf(autoCommit));
        return producerProperties;
    }


}
