package org.hifly.kafka.demo.producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KafkaConfig {

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092,localhost:9093,localhost:9094";
    private static final String CONFLUENT_SCHEMA_REGISTRY_URL =
            System.getenv("confluent.schema.registry") != null? System.getenv("confluent.schema.registry"):"http://localhost:8081";
    private static final String APICURIO_SCHEMA_REGISTRY_URL =
            System.getenv("apicurio.registry.url") != null? System.getenv("apicurio.registry.url"):"http://localhost:8080/apis/registry/v2";
    private static final String HORTONWORKS_SCHEMA_REGISTRY_URL =
            System.getenv("hortonworks.registry.url") != null? System.getenv("hortonworks.registry.url"):"http://localhost:9090/api/v1";


    public static Properties loadConfig(final String configFile) throws IOException {
        final Properties cfg = new Properties();

        ClassLoader classLoader = KafkaConfig.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(configFile);

        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + configFile);
        } else {
            cfg.load(inputStream);
        }

        return cfg;
    }
    public static Properties stringProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return producerProperties;
    }

    public static Properties stringTXProducer(String clientId, String transactionalId) {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        producerProperties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        producerProperties.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, transactionalId);
        return producerProperties;
    }


    public static Properties stringProducerCustomPartitioner() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("partitioner.class", "org.hifly.kafka.demo.producer.partitioner.custom.UserPartitioner");
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
        producerProperties.put("key.serializer", "io.apicurio.registry.serde.avro.AvroKafkaSerializer");
        producerProperties.put("value.serializer", "io.apicurio.registry.serde.avro.AvroKafkaSerializer");
        producerProperties.put("apicurio.registry.url", APICURIO_SCHEMA_REGISTRY_URL);
        producerProperties.put("apicurio.registry.auto-register", Boolean.TRUE);
        return producerProperties;
    }

    public static Properties hortonworksAvroProducer() {
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", BROKER_LIST);
        producerProperties.put("max.block.ms", 15000);
        producerProperties.put("key.serializer", StringSerializer.class.getName());
        producerProperties.put("value.serializer", "com.hortonworks.registries.schemaregistry.serdes.avro.kafka.KafkaAvroSerializer");
        producerProperties.put("schema.registry.url", HORTONWORKS_SCHEMA_REGISTRY_URL);
        return producerProperties;
    }
    
}
