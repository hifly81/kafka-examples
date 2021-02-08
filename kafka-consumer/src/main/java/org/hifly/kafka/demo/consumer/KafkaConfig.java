package org.hifly.kafka.demo.consumer;

import java.util.Properties;

public class KafkaConfig {

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092,localhost:9093,localhost:9094";


    public static Properties baseConsumerConfig(String groupId, String valueSerializerClassName, boolean autoCommit) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", BROKER_LIST);
        properties.put("group.id", groupId);
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", valueSerializerClassName);
        properties.put("enable.auto.commit", String.valueOf(autoCommit));
        properties.put("auto.offset.reset", "earliest");
        properties.put("max.poll.interval.ms", 300000);

        return properties;
    }


}
