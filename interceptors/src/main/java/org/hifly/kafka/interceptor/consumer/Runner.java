package org.hifly.kafka.interceptor.consumer;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;
import org.hifly.kafka.demo.consumer.core.ConsumerInstance;

import java.util.UUID;

public class Runner {

    public static void main (String [] args) {
        pollAutoCommit();
    }

    private static void pollAutoCommit() {

        new ConsumerInstance<String , String>(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "test_custom_data",
                StringDeserializer.class.getName(),
                CreditCardJsonDeserializer.class.getName(),
                "org.apache.kafka.clients.consumer.RangeAssignor",
                "read_uncommitted",
                100,
                500,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }
}


