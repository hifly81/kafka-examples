package org.hifly.kafka.demo.producer.serializer.json;

import org.apache.kafka.common.serialization.StringSerializer;
import org.hifly.kafka.demo.producer.AbstractKafkaProducer;
import org.hifly.kafka.demo.producer.ProducerCallback;
import org.hifly.kafka.demo.producer.KafkaConfig;
import org.hifly.kafka.demo.producer.IKafkaProducer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class JsonProducer<T> extends AbstractKafkaProducer<String, T> implements IKafkaProducer<String, T> {

    private String valueSerializer = StringSerializer.class.getName();
    
    public JsonProducer() {}

    public JsonProducer(String valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    public void start() {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.jsonProducer(valueSerializer));
    }

    @Override
    public void start(Producer<String, T> kafkaProducer) {
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
        producer.send(producerRecord, new ProducerCallback());
    }
}


