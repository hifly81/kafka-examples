package com.redhat.kafka.demo.producer;

import java.util.Properties;

public class KafkaConfig {

    public static Properties stringProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return producerProperties;
    }

    public static Properties jsonProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "com.redhat.kafka.demo.producer.serializer.json.JsonSerializer");
        return producerProperties;
    }

    public static Properties avroProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        producerProperties.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        producerProperties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        producerProperties.put("schema.registry.url", "http://localhost:8081");
        return producerProperties;
    }

    public static Properties avroPerspicuusProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092,localhost:9093,localhost:9094");
        producerProperties.put("key.serializer", "com.redhat.kafka.demo.producer.serializer.perspicuus.AvroSerializer");
        producerProperties.put("value.serializer", "com.redhat.kafka.demo.producer.serializer.perspicuus.AvroSerializer");
        return producerProperties;
    }
}
