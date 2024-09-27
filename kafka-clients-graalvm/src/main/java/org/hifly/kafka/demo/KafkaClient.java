package org.hifly.kafka.demo;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileInputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class KafkaClient {

    public static void main(String[] args) {

        Properties producerProps = new Properties();
        Properties consumerProps = new Properties();
        if(args.length == 2) {

            try (FileInputStream fis = new FileInputStream(args[0])) {
                producerProps.load(fis);
            } catch (Exception ex) {
                System.err.println("Can't load producer properties");
            }

            try (FileInputStream fis = new FileInputStream(args[1])) {
                consumerProps.load(fis);
            } catch (Exception ex) {
                System.err.println("Can't load consumer properties");
            }

        } else {
            producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        }

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        String message = "Hello GraalVM Kafka!";

        producer.send(new ProducerRecord<>("my-topic", "key", message));
        producer.close();

        System.out.println("Produced message: " + message);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singletonList("my-topic"));

        for (ConsumerRecord<String, String> record : consumer.poll(1000)) {
            System.out.println("Consumed message: " + record.value());
        }

        consumer.close();
    }
}