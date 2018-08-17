package com.redhat.kafka.demo.producer;

import java.util.Properties;

public class CustomConfig {

    public static Properties stringProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return producerProperties;
    }

    public static Properties customDataProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092");
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "com.redhat.kafka.demo.producer.serializer.CustomSerializer");
        return producerProperties;

    }
}
