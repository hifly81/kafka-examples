package com.redhat.kafka.demo.producer.serializer.perspicuus;

import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.KafkaConfig;
import com.redhat.kafka.demo.producer.KafkaProducer;
import org.apache.avro.Schema;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jboss.perspicuus.client.SchemaRegistryClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AvroDataProducer implements KafkaProducer<String, SpecificRecordBase> {


    private static Schema schema;
    private static SchemaRegistryClient schemaRegistryClient = new SchemaRegistryClient("http://localhost:8080", "testuser", "testpass");


    static {
        Schema.Parser parser = new Schema.Parser();
        try {
            ClassLoader classLoader = AvroDataProducer.class.getClassLoader();
            schema = parser.parse(new File(classLoader.getResource("car.avsc").getFile()));
            schemaRegistryClient.registerSchema(schema.getName(), schema.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Producer<String, SpecificRecordBase> producer;


    public void start() {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.avroPerspicuusProducer());

    }

    public void stop() {
        producer.close();
    }

    @Override
    public void produceFireAndForget(ProducerRecord<String, SpecificRecordBase> producerRecord) {
        producer.send(producerRecord);
    }

    @Override
    public RecordMetadata produceSync(ProducerRecord<String, SpecificRecordBase> producerRecord) {
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
        producer.send(producerRecord, new BaseProducerCallback());
    }

    public static Schema getSchema() {
        return schema;
    }

}


