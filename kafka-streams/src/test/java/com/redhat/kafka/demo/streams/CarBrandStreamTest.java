package com.redhat.kafka.demo.streams;

import com.redhat.kafka.demo.producer.serializer.base.BaseProducer;
import com.redhat.kafka.demo.streams.stream.CarBrandStream;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.streams.KafkaStreams;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.Future;

public class CarBrandStreamTest extends KafkaSuiteTest {

    private KafkaSuiteTest server;
    private BaseProducer baseProducer;
    private CarBrandStream carBrandStream;
    private String TOPIC_CAR_INFO = "topic-car-info";
    private String TOPIC_FERRARI_OUTPUT = "topic-ferrari-output";
    private String TOPIC_CAR_OUTPUT = "topic-car-output";

    @Before
    public void setup() throws Exception {
        server = new KafkaSuiteTest();
        Properties propertiesKafka = new Properties();
        propertiesKafka.put("offsets.topic.replication.factor", "1");
        propertiesKafka.put("zookeeper.connect", "localhost:2181");
        propertiesKafka.put("broker.id", "1");
        server.start(propertiesKafka);

        baseProducer = new BaseProducer();

        carBrandStream = new CarBrandStream();
        carBrandStream.start(new Properties());
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }


    @Test
    public void stream() throws Exception {
        baseProducer.start(null);

        Assert.assertNotNull(sendInfoData("1", "{\"id\":\"1\",\"brand\":\"Ferrari\",\"model\":\"Testarossa\"}"));
        Assert.assertNotNull(sendInfoData("2", "{\"id\":\"2\",\"brand\":\"Bugatti\",\"model\":\"Chiron\"}"));
        Assert.assertNotNull(sendInfoData("3", "{\"id\":\"3\",\"brand\":\"Ferrari\",\"model\":\"F40\"}"));
        Assert.assertNotNull(sendInfoData("4", "{\"id\":\"4\",\"brand\":\"Fiat\",\"model\":\"500\"}"));


        KafkaStreams kafkaStreams = carBrandStream.createStream(TOPIC_CAR_INFO, TOPIC_FERRARI_OUTPUT, TOPIC_CAR_OUTPUT);
        kafkaStreams.cleanUp();
        kafkaStreams.start();
        Thread.sleep(5000);
        kafkaStreams.close();
    }

    private Future<RecordMetadata> sendInfoData(String key, String value) {
        Future<RecordMetadata> future =  baseProducer.produceFireAndForget(
                new ProducerRecord<>(TOPIC_CAR_INFO, key, value));
        return future;
    }
}
