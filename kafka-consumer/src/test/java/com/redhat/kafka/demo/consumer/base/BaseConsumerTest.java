package com.redhat.kafka.demo.consumer.base;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BaseConsumerTest {

    private BaseConsumer baseConsumer;

    @Before
    public void setUp() {
        baseConsumer = new BaseConsumer("cons-1");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void subscribe() {
        baseConsumer.subscribe("group-1", "demo-3");
        baseConsumer.poll(100);
    }

}