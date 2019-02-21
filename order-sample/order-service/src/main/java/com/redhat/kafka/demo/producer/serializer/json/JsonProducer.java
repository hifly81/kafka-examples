package com.redhat.kafka.demo.producer.serializer.json;

import com.redhat.kafka.demo.producer.AbstractKafkaProducer;
import com.redhat.kafka.demo.producer.BaseKafkaProducer;
import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.KafkaConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class JsonProducer<T> extends AbstractKafkaProducer<String, T> implements BaseKafkaProducer<String, T> {

    public void start(Properties properties) {
        producer = new KafkaProducer(
                KafkaConfig.jsonProducer(properties.getProperty("valueSerializer")));
    }

    @Override
    public void start(Properties properties, KafkaProducer<String, T> kafkaProducer) {
        producer = kafkaProducer;
    }

    public void stop() {
        producer.close();
    }

    public Future<RecordMetadata> produceFireAndForget(ProducerRecord<String, T> producerRecord) {
        return producer.send(producerRecord);
    }

    public RecordMetadata produceSync(ProducerRecord<String, T> producerRecord) {
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
    public void produceAsync(ProducerRecord<String, T> producerRecord, Callback callback) {
        producer.send(producerRecord, new BaseProducerCallback());
    }
}


