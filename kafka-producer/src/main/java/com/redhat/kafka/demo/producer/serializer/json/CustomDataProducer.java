package com.redhat.kafka.demo.producer.serializer.json;

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

public class CustomDataProducer extends AbstractKafkaProducer<String, CustomData> implements BaseKafkaProducer<String, CustomData> {

    public void start() {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.jsonProducer());
    }

    @Override
    public void start(KafkaProducer<String, CustomData> kafkaProducer) {
        producer = kafkaProducer;
    }

    public void stop() {
        producer.close();
    }

    public Future<RecordMetadata> produceFireAndForget(ProducerRecord<String, CustomData> producerRecord) {
        if(producer == null)
            start();

        return producer.send(producerRecord);
    }

    public RecordMetadata produceSync(ProducerRecord<String, CustomData> producerRecord) {
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
    public void produceAsync(ProducerRecord<String, CustomData> producerRecord, Callback callback) {
        if(producer == null)
            start();

        producer.send(producerRecord, new BaseProducerCallback());
    }
}


