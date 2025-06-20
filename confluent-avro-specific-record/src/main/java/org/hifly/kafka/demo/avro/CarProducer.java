package org.hifly.kafka.demo.avro;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hifly.kafka.demo.avro.domain.Car;
import org.hifly.kafka.demo.producer.AbstractKafkaProducer;
import org.hifly.kafka.demo.producer.IKafkaProducer;
import org.hifly.kafka.demo.producer.ProducerCallback;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class CarProducer {

    private static final String TOPIC = "cars";

    public static void main (String [] args) {
        CarKafkaProducer carProducer = new CarKafkaProducer();
        carProducer.start();
        bunchOfMessages(TOPIC, carProducer);
    }

    public static void bunchOfMessages(String topic, CarKafkaProducer carProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 0; i < 1000; i++ ) {
            Car car = new Car();
            car.setBrand("Ferrari");
            car.setModel("F40");
            carProducer.produceAsync(new ProducerRecord<>(topic, String.valueOf(i), car), new ProducerCallback());
        }

    }

    public static class CarKafkaProducer extends AbstractKafkaProducer<String, Car> implements IKafkaProducer<String, Car> {

        private static final String BROKER_LIST =
                System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092";
        private static final String CONFLUENT_SCHEMA_REGISTRY_URL =
                System.getenv("confluent.schema.registry") != null? System.getenv("confluent.schema.registry"):"http://localhost:8081";

        @Override
        public void start() {
            Properties producerProperties = new Properties();
            producerProperties.put("bootstrap.servers", BROKER_LIST);
            producerProperties.put("max.block.ms", 15000);
            producerProperties.put(AbstractKafkaAvroSerDeConfig.AUTO_REGISTER_SCHEMAS, false);
            producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
            producerProperties.put("schema.registry.url", CONFLUENT_SCHEMA_REGISTRY_URL);
            producer = new org.apache.kafka.clients.producer.KafkaProducer(producerProperties);
        }

        @Override
        public void start(Producer<String, Car> kafkaProducer) {}

        @Override
        public void stop() {
            producer.close();
        }

        @Override
        public Future<RecordMetadata> produceFireAndForget(ProducerRecord<String, Car> producerRecord) {
            return producer.send(producerRecord);
        }

        @Override
        public RecordMetadata produceSync(ProducerRecord<String, Car> producerRecord) {
            RecordMetadata recordMetadata = null;
            try {
                recordMetadata = producer.send(producerRecord).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return recordMetadata;
        }

        @Override
        public void produceAsync(ProducerRecord<String, Car> producerRecord, Callback callback) {
            producer.send(producerRecord, new ProducerCallback());
        }
    }
}
