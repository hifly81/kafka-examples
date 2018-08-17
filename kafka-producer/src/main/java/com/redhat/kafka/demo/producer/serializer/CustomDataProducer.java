package com.redhat.kafka.demo.producer.serializer;

import com.redhat.kafka.demo.producer.CustomConfig;
import com.redhat.kafka.demo.producer.CustomProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.concurrent.ExecutionException;

public class CustomDataProducer implements CustomProducer<String, CustomData> {

    private Producer<String, CustomData> producer;

    public void start() {
        producer = new KafkaProducer<>(CustomConfig.customDataProducer());
    }

    public void stop() {
        producer.close();
    }

    public void produceFireAndForget(ProducerRecord<String, CustomData> producerRecord) {
        producer.send(producerRecord);
    }

    public RecordMetadata produceSync(ProducerRecord<String, CustomData> producerRecord) {
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


