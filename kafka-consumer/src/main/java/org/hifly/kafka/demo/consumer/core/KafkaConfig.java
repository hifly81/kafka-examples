package org.hifly.kafka.demo.consumer.core;

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


    public static Properties consumerConfig(String groupId, String keyDeserializerClassName, String valueDeserializerClassName, boolean autoCommit) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", BROKER_LIST);
        properties.put("group.id", groupId);
        properties.put("key.deserializer", keyDeserializerClassName);
        properties.put("value.deserializer", valueDeserializerClassName);
        properties.put("enable.auto.commit", String.valueOf(autoCommit));
        properties.put("auto.offset.reset", "earliest");
        properties.put("max.poll.interval.ms", 300000);

        return properties;
    }

    public static Properties consumerConfluentConfig(String groupId, String keyDeserializerClassName, String valueDeserializerClassName, boolean autoCommit) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", BROKER_LIST);
        properties.put("group.id", groupId);
        properties.put("key.deserializer", keyDeserializerClassName);
        properties.put("value.deserializer", valueDeserializerClassName);
        properties.put("schema.registry.url", CONFLUENT_SCHEMA_REGISTRY_URL);
        properties.put("enable.auto.commit", String.valueOf(autoCommit));
        properties.put("auto.offset.reset", "earliest");
        properties.put("max.poll.interval.ms", 300000);

        return properties;
    }

    public static Properties consumerApicurioConfig(String groupId, String keyDeserializerClassName, String valueDeserializerClassName, boolean autoCommit) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", BROKER_LIST);
        properties.put("group.id", groupId);
        properties.put("key.deserializer", keyDeserializerClassName);
        properties.put("value.deserializer", valueDeserializerClassName);
        properties.put("apicurio.registry.url", APICURIO_SCHEMA_REGISTRY_URL);
        properties.put("enable.auto.commit", String.valueOf(autoCommit));
        properties.put("auto.offset.reset", "earliest");
        properties.put("max.poll.interval.ms", 300000);

        return properties;
    }


}
