package org.hifly.kafka.demo.avro.references;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hifly.kafka.demo.avro.references.CarInfo;
import org.hifly.kafka.demo.avro.references.CarTelemetryData;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class RunnerProducer {

    private static final String TOPIC = "car-telemetry";

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092";
    private static final String CONFLUENT_SCHEMA_REGISTRY_URL =
            System.getenv("confluent.schema.registry") != null? System.getenv("confluent.schema.registry"):"http://localhost:8081";

    public static void main (String [] args) {

        List<SpecificRecordBase> events = new ArrayList<>();

        CarInfo carInfo = new CarInfo();
        carInfo.setModel("Ferrari");
        carInfo.setBrand("F40");

        CarTelemetryData carTelemetryData = new CarTelemetryData();
        carTelemetryData.setLatitude("42.8");
        carTelemetryData.setLongitude("22.6");
        carTelemetryData.setSpeed(156.8);

        events.add(carInfo);
        events.add(carTelemetryData);

        try (KafkaProducer<String, SpecificRecordBase> producer = new KafkaProducer<>(loadProducerConfig())) {
            for(SpecificRecordBase recordBase: events) {
                ProducerRecord<String, SpecificRecordBase> producerRecord = new ProducerRecord<>(TOPIC, "car1", recordBase);
                producer.send(producerRecord);
            }
        }

    }

    private static Properties loadProducerConfig() {

        final Properties cfg = new Properties();
        cfg.put("bootstrap.servers", BROKER_LIST);
        cfg.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        cfg.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        cfg.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
        cfg.put(AbstractKafkaSchemaSerDeConfig.USE_LATEST_VERSION, true);
        cfg.put("schema.registry.url", CONFLUENT_SCHEMA_REGISTRY_URL);

        return cfg;
    }

}
