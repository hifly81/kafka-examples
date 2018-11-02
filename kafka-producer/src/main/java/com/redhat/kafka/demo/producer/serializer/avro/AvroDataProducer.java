package com.redhat.kafka.demo.producer.serializer.avro;

import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.KafkaConfig;
import com.redhat.kafka.demo.producer.KafkaProducer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AvroDataProducer implements KafkaProducer<String, GenericRecord> {

    private Producer<String, GenericRecord> producer;
    private Schema schema;
    private GenericRecord car;


    public void start() {
        producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.avroProducer());
        Schema.Parser parser = new Schema.Parser();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            schema = parser.parse(new File(classLoader.getResource("car.avsc").getFile()));
            car = new GenericData.Record(schema);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        producer.close();
    }

    public GenericRecord getGenericRecord() {
        if(car != null)
            return new GenericData.Record(schema);
        return car;
    }

    @Override
    public void produceFireAndForget(ProducerRecord<String, GenericRecord> producerRecord) {
        producer.send(producerRecord);
    }

    @Override
    public RecordMetadata produceSync(ProducerRecord<String, GenericRecord> producerRecord) {
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
    public void produceAsync(ProducerRecord<String, GenericRecord> producerRecord, Callback callback) {
        producer.send(producerRecord, new BaseProducerCallback());
    }

}


