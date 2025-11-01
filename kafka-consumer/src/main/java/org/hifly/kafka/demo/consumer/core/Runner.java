package org.hifly.kafka.demo.consumer.core;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;

import java.util.UUID;

public class Runner {

    public static void main (String [] args) {
        String topics = null;
        String partitionStrategy = "org.apache.kafka.clients.consumer.RangeAssignor";
        String groupId = "group-1";
        if(args != null && args.length >= 1) {
            topics = args[0];
        }
        if(args != null && args.length == 2) {
            partitionStrategy = args[1];
        }
        if(args != null && args.length == 3) {
            groupId = args[2];
        }
        pollAutoCommit(topics, partitionStrategy, groupId);
    }

    private static void pollAutoCommit(String topics, String partitionStrategy, String groupId) {

        new ConsumerInstance<String , String>(
                UUID.randomUUID().toString(),
                groupId,
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
                new ConsumerHandle<String , String>(null)).consume();
    }
}


