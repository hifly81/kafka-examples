package org.hifly.kafka.demo.consumer.deserializer;

import org.hifly.kafka.demo.consumer.deserializer.impl.ConsumerHandle;
import org.hifly.kafka.demo.consumer.deserializer.impl.ConsumerThread;

public class Runner {

    public static void main (String [] args) {
        pollAutoCommit();
        //pollSyncCommit();
        //pollAssignSyncCommit();
    }

    private static void pollAutoCommit() {
        for(int i = 0; i < 3; i++) {
            //one consumer will be idle with 3 partitions
            Thread t = new Thread(
                    new ConsumerThread<String , String>(
                            String.valueOf(i),
                            "group-user-2",
                            "topic1",
                            "org.apache.kafka.common.serialization.StringDeserializer",
                            "org.apache.kafka.common.serialization.StringDeserializer",
                            100,
                            5000,
                            true ,
                            false,
                            true,
                            new ConsumerHandle(null)));
            t.start();
        }
    }

    private static void pollSyncCommit() {
        for(int i = 0; i < 4; i++) {
            //one consumer will be idle with 3 partitions
            Thread t = new Thread(
                    new ConsumerThread<String , String>(
                            String.valueOf(i),
                            "group-user-2",
                            "demo-3",
                            "org.apache.kafka.common.serialization.StringDeserializer",
                            "org.apache.kafka.common.serialization.StringDeserializer",
                            100,
                            -1,
                            false,
                            false,
                            true,
                            new ConsumerHandle(null)));
            t.start();
        }
    }

    private static void pollAssignSyncCommit() {
        for(int i = 0; i < 4; i++) {
            //one consumer will be idle with 3 partitions
            Thread t = new Thread(
                    new ConsumerThread(
                            String.valueOf(i),
                            "group-user-2",
                            "demo-3", "org.apache.kafka.common.serialization.StringDeserializer",
                            "org.apache.kafka.common.serialization.StringDeserializer",
                            100,
                            -1,
                            true,
                            false,
                            false,
                            new ConsumerHandle(null)));
            t.start();
        }
    }

}
