package org.hifly.kafka.demo.consumer.deserializer;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.deserializer.impl.ConsumerHandle;
import org.hifly.kafka.demo.consumer.deserializer.impl.ConsumerInstance;

import java.util.UUID;

public class Runner {

    public static void main (String [] args) {
        String topics = null;
        if(args != null && args.length == 1) {
            topics = args[0];
        }
        pollAutoCommit(topics);
    }

    private static void pollAutoCommit(String topics) {

        new ConsumerInstance<String , String>(
                UUID.randomUUID().toString(),
                "group-XX",
                topics == null? "topic1": topics,
                StringDeserializer.class.getName(),
                StringDeserializer.class.getName(),
                "read_uncommitted",
                100,
                -1,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }
}


