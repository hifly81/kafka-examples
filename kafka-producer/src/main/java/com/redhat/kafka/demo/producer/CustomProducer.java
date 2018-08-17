package com.redhat.kafka.demo.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public interface CustomProducer<K, V> {

    void start();

    void stop();

    void produceFireAndForget(ProducerRecord<K, V> producerRecord);

    RecordMetadata produceSync(ProducerRecord<K, V> producerRecord);

}
