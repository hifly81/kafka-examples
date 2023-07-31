package org.hifly.kafka.demo.consumer.deserializer.avro;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.core.ConsumerInstance;
import org.hifly.kafka.demo.consumer.core.KafkaConfig;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;

import java.util.UUID;

public class Runner {

    private static final String TOPIC = "test_avro_data";

    public static void main (String [] args) {
        String schemaRegistry = null;
        if(args != null && args.length >= 1) {
            schemaRegistry = args[0];
        }

        pollAutoCommit(TOPIC, schemaRegistry);
    }

    private static void pollAutoCommit(String topics, String schemaRegistry) {
        String deserializerName;

        switch (schemaRegistry) {
            default:
                deserializerName = KafkaAvroDeserializer.class.getName();
                break;
        }

        KafkaConsumer<String, GenericRecord> consumer = new KafkaConsumer<>(
                KafkaConfig.consumerAvroConfluentConfig("group-avro", StringDeserializer.class.getName(), deserializerName, true));

        new ConsumerInstance<String , GenericRecord>(
                UUID.randomUUID().toString(),
                topics == null? "topic1": topics,
                consumer,
                100,
                -1,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }
}
