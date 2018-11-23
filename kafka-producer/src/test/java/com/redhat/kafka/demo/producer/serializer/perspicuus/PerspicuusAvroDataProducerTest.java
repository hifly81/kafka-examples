package com.redhat.kafka.demo.producer.serializer.perspicuus;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

public class PerspicuusAvroDataProducerTest {

    private PerspicuusAvroDataProducer perspicuusAvroDataProducer;

    @Before
    public void setUp() {
        perspicuusAvroDataProducer = new PerspicuusAvroDataProducer();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() {
        perspicuusAvroDataProducer.start(null);
        Producer<String, SpecificRecordBase> producer = perspicuusAvroDataProducer.getProducer();
        Assert.assertNotNull(producer);
        Assert.assertNotNull(perspicuusAvroDataProducer.getSchema());

    }

    @Test
    public void stop() {
        perspicuusAvroDataProducer.start(null);
        perspicuusAvroDataProducer.stop();
    }

    @Test
    public void produceFireAndForget() {
        SpecificRecordBase specificRecordBase = new CarRecordBase();
        specificRecordBase.put("model", "1");
        specificRecordBase.put("brand", "The Best Car Company in Town");
        Future<RecordMetadata> future =  perspicuusAvroDataProducer.produceFireAndForget(new ProducerRecord<>("test", specificRecordBase));
        Assert.assertNotNull(future);
    }

    @Test
    public void produceSync() {
        SpecificRecordBase specificRecordBase = new CarRecordBase();
        specificRecordBase.put("model", "1");
        specificRecordBase.put("brand", "The Best Car Company in Town");
        RecordMetadata recordMetadata = perspicuusAvroDataProducer.produceSync(new ProducerRecord<>("test", specificRecordBase));
        Assert.assertNotNull(recordMetadata);
        Assert.assertTrue(recordMetadata.hasTimestamp());
        Assert.assertTrue(recordMetadata.hasOffset());
    }

}