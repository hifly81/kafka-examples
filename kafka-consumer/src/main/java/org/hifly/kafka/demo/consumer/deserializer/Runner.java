package org.hifly.kafka.demo.consumer.deserializer;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.deserializer.impl.ConsumerHandle;
import org.hifly.kafka.demo.consumer.deserializer.impl.ConsumerInstance;

import java.util.UUID;

public class Runner {

    public static void main (String [] args) {
        pollAutoCommit();
    }

    private static void pollAutoCommit() {

        new ConsumerInstance<String , String>(
                "1",
                UUID.randomUUID().toString(),
                "topic1",
                StringDeserializer.class.getName(),
                StringDeserializer.class.getName(),
                100,
                500,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();

    }
}


