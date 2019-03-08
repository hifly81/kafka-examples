package com.redhat.kafka.demo.streams;

import com.redhat.kafka.demo.producer.serializer.base.BaseProducer;
import com.redhat.kafka.demo.streams.stream.CarSensorStream;
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
    private String TOPIC_CAR_SENSOR = "topic-car-sensor";
    private String TOPIC_CAR_INFO = "topic-car-info";
    private String TOPIC_OUT = "topic-out";

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
                new ProducerRecord<>(TOPIC_CAR_SENSOR, "1", "{\"id\":\"1\",\"lat\":12.657,\"lng\":25.543,\"speed\":350}"));
        Assert.assertNotNull(future);

        Future<RecordMetadata> future2 =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC_CAR_SENSOR, "2", "{\"id\":\"2\",\"lat\":16.657,\"lng\":23.543,\"speed\":360}"));
        Assert.assertNotNull(future2);

        Future<RecordMetadata> future3 =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC_CAR_SENSOR, "1", "{\"id\":\"1\",\"lat\":13.657,\"lng\":23.582,\"speed\":370}"));
        Assert.assertNotNull(future3);

        Future<RecordMetadata> future4 =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC_CAR_INFO, "1", "{\"id\":\"1\",\"brand\":\"Ferrari\",\"model\":\"Testarossa\"}"));
        Assert.assertNotNull(future4);

        Future<RecordMetadata> future5 =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC_CAR_INFO, "2", "{\"id\":\"2\",\"brand\":\"Bugatti\",\"model\":\"Chiron\"}"));
        Assert.assertNotNull(future5);

        Future<RecordMetadata> future6 =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC_CAR_INFO, "1", "{\"id\":\"1\",\"brand\":\"Ferrari\",\"model\":\"Testarossa\"}"));
        Assert.assertNotNull(future6);


        carSensorStream.stream(TOPIC_CAR_SENSOR, TOPIC_CAR_INFO, TOPIC_OUT, 10000);
    }
}
