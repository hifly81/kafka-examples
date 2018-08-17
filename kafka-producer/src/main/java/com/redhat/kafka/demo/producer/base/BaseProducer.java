package com.redhat.kafka.demo.producer.base;

import com.redhat.kafka.demo.producer.CustomConfig;
import com.redhat.kafka.demo.producer.CustomProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;

public class BaseProducer implements CustomProducer<String, String> {

    private Producer<String, String> producer;


    public void start() {
        producer = new KafkaProducer<>(CustomConfig.stringProducer());
    }

    public void stop() {
        producer.close();
    }

    public void produceFireAndForget(ProducerRecord<String, String> producerRecord) {
        producer.send(producerRecord);
    }

    public RecordMetadata produceSync(ProducerRecord<String, String> producerRecord) {
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
}


