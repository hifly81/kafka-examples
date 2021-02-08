package org.hifly.kafka.demo.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.Future;

public interface BaseKafkaProducer<K, V> {

    void start();

    void start(Producer<K, V> kafkaProducer);

    void stop();

    Future<RecordMetadata> produceFireAndForget(ProducerRecord<K, V> producerRecord);

    RecordMetadata produceSync(ProducerRecord<K, V> producerRecord);

    void produceAsync(ProducerRecord<K, V> producerRecord, Callback callback);

}
