package com.redhat.kafka.demo.consumer.base;

import com.redhat.kafka.demo.consumer.BaseConsumer;
import com.redhat.kafka.demo.consumer.handle.BaseConsumerHandle;
import com.redhat.kafka.demo.producer.serializer.base.BaseProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.Future;

public class BaseConsumerTest {

    private BaseConsumer<String> baseConsumer;
    private BaseProducer baseProducer;
    private KafkaSuiteTest server;
    private String TOPIC = "topic-test-1";

    @Before
    public void setUp() throws Exception {
        server = new KafkaSuiteTest();
        server.start();
        baseProducer = new BaseProducer();
        Properties properties = new Properties();
        properties.setProperty("desererializerClass", "org.apache.kafka.common.serialization.StringDeserializer");
        baseConsumer = new BaseConsumer<>("cons-1", properties, new BaseConsumerHandle());
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void subscribe() {
        baseProducer.start(null);
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(new ProducerRecord<>(TOPIC, "dummy"));
        Assert.assertNotNull(future);
        baseConsumer.subscribe("group-1", TOPIC, true);
        baseConsumer.poll(100, -1, false);
    }

    @Test
    public void subscribeSyncCommit() {
        baseProducer.start(null);
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(new ProducerRecord<>(TOPIC, "dummy"));
        Assert.assertNotNull(future);
        baseConsumer.subscribe("group-1", TOPIC, false);
        baseConsumer.poll(100, -1, false);
    }

    @Test
    public void assignSyncCommit() {
        baseProducer.start(null);
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(new ProducerRecord<>(TOPIC, "dummy"));
        Assert.assertNotNull(future);
        Assert.assertTrue(baseConsumer.assign(TOPIC, null, true));
        baseConsumer.poll(100, -1,false);
    }

}