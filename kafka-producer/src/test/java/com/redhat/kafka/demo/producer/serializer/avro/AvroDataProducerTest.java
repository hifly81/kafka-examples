package com.redhat.kafka.demo.producer.serializer.avro;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

public class AvroDataProducerTest {

    private AvroDataProducer avroDataProducer;

    @Before
    public void setUp() {
        avroDataProducer = new AvroDataProducer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() {
        avroDataProducer.start();
        Producer<String, GenericRecord> producer = avroDataProducer.getProducer();
        Assert.assertNotNull(producer);
        Assert.assertNotNull(avroDataProducer.getCar());
        Assert.assertNotNull(avroDataProducer.getSchema());

    }

    @Test
    public void stop() {
        avroDataProducer.start();
        avroDataProducer.stop();
    }

    @Test
    public void produceFireAndForget() {
        GenericRecord genericRecord = avroDataProducer.getGenericRecord();
        genericRecord.put("model", "1");
        genericRecord.put("brand", "The Best Car Company in Town");
        Future<RecordMetadata> future =  avroDataProducer.produceFireAndForget(new ProducerRecord<>("test", genericRecord));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        GenericRecord genericRecord = avroDataProducer.getGenericRecord();
        genericRecord.put("model", "1");
        genericRecord.put("brand", "The Best Car Company in Town");
        RecordMetadata recordMetadata = avroDataProducer.produceSync(new ProducerRecord<>("test", genericRecord));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
    }

}