package com.redhat.kafka.demo.producer.partitioner.custom;

import com.redhat.kafka.demo.producer.KafkaConfig;
import com.redhat.kafka.demo.producer.serializer.base.BaseProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
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
    private KafkaProducer<String, String> kafkaProducer;
    private String topicName = "demo-3";

    @Before
    public void setUp() {
        baseProducer = new BaseProducer();
        kafkaProducer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.stringProducerCustomPartitioner());
    }

    @After
    public void tearDown() {
    }

    @Test
    public void start() {
        baseProducer.start(null, kafkaProducer);
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
    }

    @Test
    public void stop() {
        baseProducer.start(null, kafkaProducer);
        baseProducer.stop();
    }

    @Test
    public void produceFireAndForget() {
        baseProducer.start(null, kafkaProducer);
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(new ProducerRecord<>(topicName, "Mark","dummy"));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        baseProducer.start(null, kafkaProducer);
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
        RecordMetadata recordMetadata = baseProducer.produceSync(new ProducerRecord<>(topicName, "Mark","dummy"));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
        Assert.assertEquals(0, recordMetadata.partition());

        RecordMetadata recordMetadata2 = baseProducer.produceSync(new ProducerRecord<>(topicName, "Antony","dummy"));
        Assert.assertNotNull(recordMetadata2);
        Assert.assertTrue(recordMetadata2.hasTimestamp());
        Assert.assertTrue(recordMetadata2.hasOffset());
        Assert.assertEquals(1, recordMetadata2.partition());

        RecordMetadata recordMetadata3 = baseProducer.produceSync(new ProducerRecord<>(topicName, "Paul","dummy"));
        Assert.assertNotNull(recordMetadata3);
        Assert.assertTrue(recordMetadata3.hasTimestamp());
        Assert.assertTrue(recordMetadata3.hasOffset());
        Assert.assertEquals(2, recordMetadata3.partition());

        RecordMetadata recordMetadata4 = baseProducer.produceSync(new ProducerRecord<>(topicName, "Mike","dummy"));
        Assert.assertNotNull(recordMetadata4);
        Assert.assertTrue(recordMetadata4.hasTimestamp());
        Assert.assertTrue(recordMetadata4.hasOffset());
        Assert.assertEquals(0, recordMetadata4.partition());
    }

}