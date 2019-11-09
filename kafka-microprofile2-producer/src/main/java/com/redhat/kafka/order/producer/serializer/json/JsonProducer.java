package com.redhat.kafka.order.producer.serializer.json;

import com.redhat.kafka.order.producer.AbstractKafkaProducer;
import com.redhat.kafka.order.producer.BaseKafkaProducer;
import com.redhat.kafka.order.producer.BaseProducerCallback;
import com.redhat.kafka.order.producer.KafkaConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class JsonProducer<T> extends AbstractKafkaProducer<String, T> implements BaseKafkaProducer<String, T> {

    private Logger log = LoggerFactory.getLogger(JsonProducer.class);

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
            log.info("Send record to kafka {}", producerRecord.key());
            recordMetadata = producer.send(producerRecord).get();
        } catch (InterruptedException e) {
            log.error("Error in produceSync!", e);
        } catch (ExecutionException e) {
            log.error("Error in produceSync!", e);
        }
        return recordMetadata;
    }

    @Override
    public void produceAsync(ProducerRecord<String, T> producerRecord, Callback callback) {
        producer.send(producerRecord, new BaseProducerCallback());
    }
}


