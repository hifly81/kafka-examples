package org.hifly.kafka.demo.consumer.deserializer.avro;

import io.apicurio.registry.serde.avro.AvroKafkaDeserializer;
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

        pollAutoCommit(schemaRegistry);
    }

    private static void pollAutoCommit(String schemaRegistry) {
        SchemaRegistry schemaRegistryEnum = SchemaRegistry.valueOf(schemaRegistry);
        String deserializerName;
        KafkaConsumer<String, GenericRecord> consumer = null;

        UUID uuid = UUID. randomUUID();
        String uuidAsString = uuid. toString();

        switch (schemaRegistryEnum) {
            case CONFLUENT:
                deserializerName = KafkaAvroDeserializer.class.getName();
                consumer = new KafkaConsumer<>(
                        KafkaConfig.consumerConfluentConfig(uuidAsString, StringDeserializer.class.getName(), deserializerName, true));
                break;
            case APICURIO:
                deserializerName = AvroKafkaDeserializer.class.getName();
                consumer = new KafkaConsumer<>(
                        KafkaConfig.consumerApicurioConfig(uuidAsString, StringDeserializer.class.getName(), deserializerName, true));
                break;
            case HORTONWORKS:
                break;
            default:
                deserializerName = KafkaAvroDeserializer.class.getName();
                consumer = new KafkaConsumer<>(
                        KafkaConfig.consumerConfluentConfig(uuidAsString, StringDeserializer.class.getName(), deserializerName, true));
                break;
        }


        new ConsumerInstance<String , GenericRecord>(
                UUID.randomUUID().toString(),
                Runner.TOPIC,
                consumer,
                100,
                -1,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }
}
