package com.redhat.kafka.demo.producer.serializer.base;

import com.redhat.kafka.demo.producer.AbstractKafkaProducer;
import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.KafkaConfig;
import com.redhat.kafka.demo.producer.BaseKafkaProducer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BaseProducer extends AbstractKafkaProducer<String, String> implements BaseKafkaProducer<String, String> {

    public void start() {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.stringProducer());
    }

    @Override
    public void start(KafkaProducer<String, String> kafkaProducer) {
        producer = kafkaProducer;
    }

    public void stop() {
        producer.close();
    }

    public Future<RecordMetadata> produceFireAndForget(ProducerRecord<String, String> producerRecord) {
        if(producer == null)
            start();

        return producer.send(producerRecord);
    }

    public RecordMetadata produceSync(ProducerRecord<String, String> producerRecord) {
        if(producer == null)
            start();

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
        if(producer == null)
            start();

        producer.send(producerRecord, new BaseProducerCallback());
    }

}


