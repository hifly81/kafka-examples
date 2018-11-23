package com.redhat.kafka.demo.consumer.base;

import com.redhat.kafka.demo.consumer.BaseConsumer;
import com.redhat.kafka.demo.consumer.handle.BaseConsumerHandle;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class BaseConsumerTest {

    private BaseConsumer<String> baseConsumer;

    @Before
    public void setUp() {
        Properties properties = new Properties();
        properties.setProperty("desererializerClass", "org.apache.kafka.common.serialization.StringDeserializer");
        baseConsumer = new BaseConsumer<>("cons-1", properties, new BaseConsumerHandle());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribe() {
        baseConsumer.subscribe("group-1", "demo-3", true);
        baseConsumer.poll(100, -1, false);
    }

    @Test
    public void subscribeSyncCommit() {
        baseConsumer.subscribe("group-1", "demo-3", false);
        baseConsumer.poll(100, -1, false);
    }

    @Test
    public void assignSyncCommit() {
        Assert.assertTrue(baseConsumer.assign("demo-3", null, true));
        baseConsumer.poll(100, -1,false);
    }

}