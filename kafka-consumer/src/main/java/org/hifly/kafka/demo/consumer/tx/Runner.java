package org.hifly.kafka.demo.consumer.tx;

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
                "test-idempotent",
                StringDeserializer.class.getName(),
                StringDeserializer.class.getName(),
                "org.apache.kafka.clients.consumer.RangeAssignor",
                "read_committed",
                100,
                -1,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }
}


