package com.redhat.kafka.demo.producer.partitioner.custom;

import com.redhat.kafka.demo.producer.KafkaConfig;
import com.redhat.kafka.demo.producer.KafkaSuiteTest;
import com.redhat.kafka.demo.producer.serializer.base.BaseProducer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.Future;

public class BaseProducerTest {

    private KafkaSuiteTest server;
    private BaseProducer baseProducer;
    private KafkaProducer<String, String> kafkaProducer;
    private String TOPIC = "topic-test-3";

    @Before
    public void setUp() throws Exception {
        server = new KafkaSuiteTest();
        Properties props = new Properties();
        props.put("zookeeper.connect", "localhost:2181");
        props.put("broker.id", "1");
        props.put("num.partitions", 3);
        server.start(props);
        baseProducer = new BaseProducer();
        kafkaProducer = new KafkaProducer(KafkaConfig.stringProducerCustomPartitioner());
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test
    public void start() {
        baseProducer.start(kafkaProducer);
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
    }

    @Test
    public void produceFireAndForget() {
        baseProducer.start(kafkaProducer);
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(new ProducerRecord<>(TOPIC, "Mark","dummy"));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        baseProducer.start(kafkaProducer);
        Producer<String, String> producer = baseProducer.getProducer();
        Assert.assertNotNull(producer);
        RecordMetadata recordMetadata = baseProducer.produceSync(new ProducerRecord<>(TOPIC, "Mark","dummy"));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
        Assert.assertEquals(0, recordMetadata.partition());

        RecordMetadata recordMetadata2 = baseProducer.produceSync(new ProducerRecord<>(TOPIC, "Antony","dummy"));
        Assert.assertNotNull(recordMetadata2);
        Assert.assertTrue(recordMetadata2.hasTimestamp());
        Assert.assertTrue(recordMetadata2.hasOffset());
        Assert.assertEquals(1, recordMetadata2.partition());

        RecordMetadata recordMetadata3 = baseProducer.produceSync(new ProducerRecord<>(TOPIC, "Paul","dummy"));
        Assert.assertNotNull(recordMetadata3);
        Assert.assertTrue(recordMetadata3.hasTimestamp());
        Assert.assertTrue(recordMetadata3.hasOffset());
        Assert.assertEquals(2, recordMetadata3.partition());

        RecordMetadata recordMetadata4 = baseProducer.produceSync(new ProducerRecord<>(TOPIC, "Mike","dummy"));
        Assert.assertNotNull(recordMetadata4);
        Assert.assertTrue(recordMetadata4.hasTimestamp());
        Assert.assertTrue(recordMetadata4.hasOffset());
        Assert.assertEquals(0, recordMetadata4.partition());
    }

}