package org.hifly.kafka.demo.consumer.deserializer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.deserializer.impl.ConsumerHandle;
import org.hifly.kafka.demo.consumer.deserializer.impl.ConsumerInstance;

import java.util.UUID;

public class Runner {

    public static void main (String [] args) {
        String topics = null;
        String partitionStrategy = "org.apache.kafka.clients.consumer.RangeAssignor";
        if(args != null && args.length >= 1) {
            topics = args[0];
        }
        if(args.length == 2) {
            partitionStrategy = args[1];
        }
        pollAutoCommit(topics, partitionStrategy);
    }

    private static void pollAutoCommit(String topics, String partitionStrategy) {
        new ConsumerInstance<String , String>(
                UUID.randomUUID().toString(),
                "group-XX",
                topics == null? "topic1": topics,
                StringDeserializer.class.getName(),
                StringDeserializer.class.getName(),
                partitionStrategy,
                "read_uncommitted",
                100,
                -1,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }
}


