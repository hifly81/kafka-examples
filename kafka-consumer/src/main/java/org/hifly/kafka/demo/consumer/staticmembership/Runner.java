package org.hifly.kafka.demo.consumer.staticmembership;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.hifly.kafka.demo.consumer.core.ConsumerInstance;
import org.hifly.kafka.demo.consumer.core.KafkaConfig;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;

import java.io.IOException;
import java.util.UUID;

public class Runner {

    public static void main (String [] args) throws Exception {
        String fileName = args[0];

        pollAutoCommit(fileName);
    }

    private static void pollAutoCommit(String fileName) throws IOException {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                KafkaConfig.loadConfig(fileName));

        new ConsumerInstance<String , String>(
                UUID.randomUUID().toString(),
                "topic1",
                consumer,
                100,
                3000000,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }


}