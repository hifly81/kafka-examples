package com.redhat.kafka.demo.producer.serializer.base;

import com.redhat.kafka.demo.producer.KafkaSuiteTest;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

public class BaseProducerTest extends KafkaSuiteTest {

    private KafkaSuiteTest server;
    private BaseProducer baseProducer;
    private String TOPIC = "topic-test-1";

    @Before
    public void setup() throws Exception {
        server = new KafkaSuiteTest();
        server.start();
        baseProducer = new BaseProducer();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }


    @Test
    public void start() {
        baseProducer.start(null);
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
    }

    @Test
    public void produceFireAndForget() {
        baseProducer.start(null);
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(new ProducerRecord<>(TOPIC, "dummy"));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        baseProducer.start(null);
        RecordMetadata recordMetadata = baseProducer.produceSync(new ProducerRecord<>(TOPIC, "dummy"));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
    }

}