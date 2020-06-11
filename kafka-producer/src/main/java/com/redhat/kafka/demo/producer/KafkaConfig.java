package com.redhat.kafka.demo.producer;

import java.util.Properties;

public class KafkaConfig {

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092,localhost:9093,localhost:9094";
    private static final String CONFLUENT_SCHEMA_REGISTRY_URL =
            System.getenv("confluent.schema.registry") != null? System.getenv("confluent.schema.registry"):"http://localhost:8081";
    private static final String APICURIO_SCHEMA_REGISTRY_URL =
            System.getenv("apicurio.schema.registry") != null? System.getenv("apicurio.schema.registry"):"http://localhost:8081/api";


    public static Properties stringProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return producerProperties;
    }

    public static Properties stringProducerCustomPartitioner() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("partitioner.class", "com.redhat.kafka.demo.producer.partitioner.custom.UserPartitioner");
        return producerProperties;
    }

    public static Properties jsonProducer(String valueSerializerClassName) {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", valueSerializerClassName);
        return producerProperties;
    }

    public static Properties confluentAvroProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        producerProperties.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        producerProperties.put("schema.registry.url", CONFLUENT_SCHEMA_REGISTRY_URL);
        return producerProperties;
    }

    public static Properties apicurioAvroProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "io.apicurio.registry.utils.serde.AvroKafkaSerializer");
        producerProperties.put("value.serializer", "io.apicurio.registry.utils.serde.AvroKafkaSerializer");
        producerProperties.put("schema.registry.url", APICURIO_SCHEMA_REGISTRY_URL);
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
