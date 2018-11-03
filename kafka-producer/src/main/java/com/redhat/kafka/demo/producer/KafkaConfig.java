package com.redhat.kafka.demo.producer;

import java.util.Properties;

public class KafkaConfig {

    private static final String BROKER_LIST = "localhost:9092,localhost:9093,localhost:9094";
    private static final String SCHEMA_REGISTRY_CONFLUENT_URL = "http://localhost:8081";

    public static Properties stringProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return producerProperties;
    }

    public static Properties jsonProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "com.redhat.kafka.demo.producer.serializer.json.JsonSerializer");
        return producerProperties;
    }

    public static Properties avroProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        producerProperties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        producerProperties.put("schema.registry.url", SCHEMA_REGISTRY_CONFLUENT_URL);
        return producerProperties;
    }

    public static Properties avroPerspicuusProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "com.redhat.kafka.demo.producer.serializer.perspicuus.AvroSerializer");
        producerProperties.put("value.serializer", "com.redhat.kafka.demo.producer.serializer.perspicuus.AvroSerializer");
        return producerProperties;
    }
}
