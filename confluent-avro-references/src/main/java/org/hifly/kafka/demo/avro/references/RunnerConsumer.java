package org.hifly.kafka.demo.avro.references;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class RunnerConsumer {

    private static final String TOPIC = "car-telemetry";
    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092";
    private static final String CONFLUENT_SCHEMA_REGISTRY_URL =
            System.getenv("confluent.schema.registry") != null? System.getenv("confluent.schema.registry"):"http://localhost:8081";


    public static void main (String [] args) {

        try (KafkaConsumer<String, SpecificRecordBase> consumer = new KafkaConsumer<>(loadConsumerConfig())) {
            consumer.subscribe(Collections.singletonList(TOPIC));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> consumerRecords = consumer.poll(Duration.ofSeconds(1));
                consumerRecords.forEach(consumerRec -> {
                    SpecificRecord avroRecord = consumerRec.value();
                    if (avroRecord instanceof CarInfo) {
                        CarInfo carInfo = (CarInfo) avroRecord;
                        System.out.printf("Car Info event %s - offset-> %s", carInfo, consumerRec.offset());
                    } else if (avroRecord instanceof CarTelemetryData) {
                        CarTelemetryData carTelemetryData = (CarTelemetryData) avroRecord;
                        System.out.printf("Car Telemetry event %s - offset-> %s", carTelemetryData, consumerRec.offset());
                    } else {
                        System.out.printf("Event not supported!");
                    }
                });
            }
        }
    }

    private static Properties loadConsumerConfig() {

        final Properties cfg = new Properties();
        cfg.put("bootstrap.servers", BROKER_LIST);
        cfg.put(ConsumerConfig.GROUP_ID_CONFIG, "car-telemetry-group-1");
        cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class);
        cfg.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        cfg.put("schema.registry.url", CONFLUENT_SCHEMA_REGISTRY_URL);

        return cfg;
    }
}


