package com.redhat.kafka.demo.producer.base;

import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.KafkaConfig;
import com.redhat.kafka.demo.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;

public class BaseProducer implements KafkaProducer<String, String> {

    private Producer<String, String> producer;


    public void start() {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.stringProducer());
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

    @Override
    public void produceAsync(ProducerRecord<String, String> producerRecord, Callback callback) {
        producer.send(producerRecord, new BaseProducerCallback());
    }

}


