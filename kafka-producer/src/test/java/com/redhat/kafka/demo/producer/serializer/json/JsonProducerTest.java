package com.redhat.kafka.demo.producer.serializer.json;

import com.redhat.kafka.demo.producer.KafkaSuiteTest;
import com.redhat.kafka.demo.producer.serializer.model.CustomData;
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

    private KafkaSuiteTest server;
    private JsonProducer<CustomData> jsonProducer;
    private String TOPIC = "topic-test-1";

    @Before
    public void setUp() throws Exception {
        server = new KafkaSuiteTest();
        server.start();
        jsonProducer = new JsonProducer<>();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
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
    public void produceFireAndForget() {
        Properties properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.demo.producer.serializer.json.CustomDataJsonSerializer");
        jsonProducer.start(properties);
        CustomData customData = new CustomData();
        customData.setIndex(1);
        Future<RecordMetadata> future =  jsonProducer.produceFireAndForget(new ProducerRecord<>(TOPIC, customData));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        Properties properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.demo.producer.serializer.json.CustomDataJsonSerializer");
        jsonProducer.start(properties);
        CustomData customData = new CustomData();
        customData.setIndex(1);
        RecordMetadata recordMetadata = jsonProducer.produceSync(new ProducerRecord<>(TOPIC, customData));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
    }

}