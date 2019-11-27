package com.redhat.kafka.demo.orders.kafka.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.Future;

public interface BaseKafkaProducer<K, V> {

    void start(Properties properties);

    void start(Properties properties, KafkaProducer<K, V> kafkaProducer);

    void stop();

    Future<RecordMetadata> produceFireAndForget(ProducerRecord<K, V> producerRecord);

    RecordMetadata produceSync(ProducerRecord<K, V> producerRecord);

    void produceAsync(ProducerRecord<K, V> producerRecord, Callback callback);

}
