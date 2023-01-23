package org.hifly.kafka.demo.producer.tx;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.hifly.kafka.demo.producer.AbstractKafkaProducer;
import org.hifly.kafka.demo.producer.IKafkaProducer;
import org.hifly.kafka.demo.producer.KafkaConfig;
import org.hifly.kafka.demo.producer.ProducerCallback;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class StringTXProducer extends AbstractKafkaProducer<String, String> implements IKafkaProducer<String, String> {

    public void start() {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.stringTXProducer("testClient", "testTx"));
    }

    @Override
    public void start(Producer<String, String> kafkaProducer) {
        producer = kafkaProducer;
    }

    public void stop() {
        producer.close();
    }

    public Future<RecordMetadata> produceFireAndForget(ProducerRecord<String, String> producerRecord) {
        return producer.send(producerRecord);
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
        producer.send(producerRecord, new ProducerCallback());
    }

}


