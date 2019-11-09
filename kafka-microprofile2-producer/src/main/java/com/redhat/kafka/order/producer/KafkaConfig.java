package com.redhat.kafka.order.producer;

import java.util.Properties;

public class KafkaConfig {

    private static final String BROKER_LIST =
            System.getenv("KAFKABROKERLIST") != null? System.getenv("KAFKABROKERLIST") :"localhost:9092,localhost:9093,localhost:9094";

    public static Properties jsonProducer(String valueSerializerClassName) {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", valueSerializerClassName);
        return producerProperties;
    }

}
