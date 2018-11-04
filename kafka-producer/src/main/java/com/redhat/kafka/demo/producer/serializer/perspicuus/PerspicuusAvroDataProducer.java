package com.redhat.kafka.demo.producer.serializer.perspicuus;

import com.redhat.kafka.demo.producer.AbstractKafkaProducer;
import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.KafkaConfig;
import com.redhat.kafka.demo.producer.BaseKafkaProducer;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jboss.perspicuus.client.SchemaRegistryClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PerspicuusAvroDataProducer extends AbstractKafkaProducer<String, SpecificRecordBase> implements BaseKafkaProducer<String, SpecificRecordBase> {

    private static Schema schema;
    private static SchemaRegistryClient schemaRegistryClient = new SchemaRegistryClient("http://localhost:8080", "testuser", "testpass");


    static {
        Schema.Parser parser = new Schema.Parser();
        try {
            ClassLoader classLoader = PerspicuusAvroDataProducer.class.getClassLoader();
            schema = parser.parse(new File(classLoader.getResource("car.avsc").getFile()));
            schemaRegistryClient.registerSchema(schema.getName(), schema.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.avroPerspicuusProducer());

    }

    @Override
    public void start(KafkaProducer<String, SpecificRecordBase> kafkaProducer) {
        producer = kafkaProducer;
    }

    public void stop() {
        producer.close();
    }

    @Override
    public Future<RecordMetadata> produceFireAndForget(ProducerRecord<String, SpecificRecordBase> producerRecord) {
        if(producer == null)
            start();

        return producer.send(producerRecord);
    }

    @Override
    public RecordMetadata produceSync(ProducerRecord<String, SpecificRecordBase> producerRecord) {
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
    public void produceAsync(ProducerRecord<String, SpecificRecordBase> producerRecord, Callback callback) {
        if(producer == null)
            start();

        producer.send(producerRecord, new BaseProducerCallback());
    }

    public static Schema getSchema() {
        return schema;
    }

}


