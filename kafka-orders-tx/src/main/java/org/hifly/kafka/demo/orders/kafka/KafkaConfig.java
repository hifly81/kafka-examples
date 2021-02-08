package org.hifly.kafka.demo.orders.kafka;

import java.util.Properties;

public class KafkaConfig {

    private static final String BROKER_LIST =
            System.getenv("KAFKABROKERLIST") != null? System.getenv("KAFKABROKERLIST") :"localhost:9092,localhost:9093,localhost:9094";

    public static Properties jsonProducer(String valueSerializerClassName, String txId) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", BROKER_LIST);
        properties.put("max.block.ms", 15000);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", valueSerializerClassName);
        properties.put("enable.idempotence", "true");
        properties.put("transactional.id", txId);
        return properties;
    }

    public static Properties jsonConsumer(String groupId, String valueDeserializerClassName) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", BROKER_LIST);
        properties.put("group.id", groupId);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", valueDeserializerClassName);
        properties.setProperty("enable.auto.commit", "false");
        properties.put("isolation.level", "read_committed");
        return properties;
    }

}
