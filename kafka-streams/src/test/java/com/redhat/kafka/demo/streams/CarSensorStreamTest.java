package com.redhat.kafka.demo.streams;

import com.redhat.kafka.demo.producer.serializer.base.BaseProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.Future;

public class CarSensorStreamTest extends KafkaSuiteTest {

    private KafkaSuiteTest server;
    private BaseProducer baseProducer;
    private CarSensorStream carSensorStream;
    private String TOPIC = "topic-test-1";
    private String TOPIC_OUT = "topic-test-1-out";

    @Before
    public void setup() throws Exception {
        server = new KafkaSuiteTest();
        Properties propertiesKafka = new Properties();
        propertiesKafka.put("offsets.topic.replication.factor", "1");
        propertiesKafka.put("zookeeper.connect", "localhost:2181");
        propertiesKafka.put("broker.id", "1");
        server.start(propertiesKafka);

        baseProducer = new BaseProducer();

        carSensorStream = new CarSensorStream();
        carSensorStream.start(new Properties());
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }


    @Test
    public void stream() throws Exception {
        baseProducer.start(null);
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC, "1", "{\"id\":\"1\",\"brand\":\"Ferrari\",\"model\":\"Testarossa\",\"speed\":350}"));
        Assert.assertNotNull(future);

        Future<RecordMetadata> future2 =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC, "2", "{\"id\":\"2\",\"brand\":\"Bugatti\",\"model\":\"Chiron\",\"speed\":360}"));
        Assert.assertNotNull(future2);

        Future<RecordMetadata> future3 =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC, "1", "{\"id\":\"1\",\"brand\":\"Ferrari\",\"model\":\"Testarossa\",\"speed\":370}"));
        Assert.assertNotNull(future3);


        carSensorStream.stream(TOPIC, TOPIC_OUT, 10000);
    }
}
