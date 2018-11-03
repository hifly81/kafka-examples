package com.redhat.kafka.demo.producer.serializer.base;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

public class BaseProducerTest {

    private BaseProducer baseProducer;

    @Before
    public void setUp() {
        baseProducer = new BaseProducer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() {
        baseProducer.start();
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
    }

    @Test
    public void stop() {
        baseProducer.start();
        baseProducer.stop();
    }

    @Test
    public void produceFireAndForget() {
        baseProducer.start();
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(new ProducerRecord<>("test", "dummy"));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        baseProducer.start();
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
        RecordMetadata recordMetadata = baseProducer.produceSync(new ProducerRecord<>("test", "dummy"));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
    }

}