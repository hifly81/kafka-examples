package com.redhat.kafka.demo.producer.serializer.json;

import com.redhat.kafka.demo.producer.serializer.base.BaseProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

public class CustomDataProducerTest {

    private CustomDataProducer customDataProducer;

    @Before
    public void setUp() {
        customDataProducer = new CustomDataProducer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() {
        customDataProducer.start();
        Producer<String, CustomData> producer = customDataProducer.getProducer();
        Assert.assertNotNull(producer);
    }

    @Test
    public void stop() {
        customDataProducer.start();
        customDataProducer.stop();
    }

    @Test
    public void produceFireAndForget() {
        customDataProducer.start();
        Producer<String, CustomData> producer = customDataProducer.getProducer();
        Assert.assertNotNull(producer);
        CustomData customData = new CustomData();
        customData.setIndex(1);
        Future<RecordMetadata> future =  customDataProducer.produceFireAndForget(new ProducerRecord<>("test", customData));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        customDataProducer.start();
        Producer<String, CustomData> producer = customDataProducer.getProducer();
        Assert.assertNotNull(producer);
        CustomData customData = new CustomData();
        customData.setIndex(1);
        RecordMetadata recordMetadata = customDataProducer.produceSync(new ProducerRecord<>("test", customData));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
    }

}