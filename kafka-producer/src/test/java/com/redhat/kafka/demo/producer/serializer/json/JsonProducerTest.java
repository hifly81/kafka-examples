package com.redhat.kafka.demo.producer.serializer.json;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.Future;

public class JsonProducerTest {

    private JsonProducer<CustomData> jsonProducer;

    @Before
    public void setUp() {
        jsonProducer = new JsonProducer<CustomData>();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() {
        Properties properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.demo.producer.serializer.json.CustomDataJsonSerializer");
        jsonProducer.start(properties);
        Producer<String, CustomData> producer = jsonProducer.getProducer();
        Assert.assertNotNull(producer);
    }

    @Test
    public void stop() {
        Properties properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.demo.producer.serializer.json.CustomDataJsonSerializer");
        jsonProducer.start(properties);
        jsonProducer.stop();
    }

    @Test
    public void produceFireAndForget() {
        CustomData customData = new CustomData();
        customData.setIndex(1);
        Future<RecordMetadata> future =  jsonProducer.produceFireAndForget(new ProducerRecord<>("test", customData));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        CustomData customData = new CustomData();
        customData.setIndex(1);
        RecordMetadata recordMetadata = jsonProducer.produceSync(new ProducerRecord<>("test", customData));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
    }

}