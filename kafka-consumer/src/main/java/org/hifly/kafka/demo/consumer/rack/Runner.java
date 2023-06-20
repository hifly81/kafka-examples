package org.hifly.kafka.demo.consumer.rack;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.hifly.kafka.demo.consumer.core.ConsumerInstance;
import org.hifly.kafka.demo.consumer.core.KafkaConfig;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;

import java.io.IOException;
import java.util.UUID;

public class Runner {

    public static void main (String [] args) throws Exception {
        pollAutoCommit();
    }

    private static void pollAutoCommit() throws IOException {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                KafkaConfig.loadConfig("consumer-ffetching.properties"));

        new ConsumerInstance<String , String>(
                UUID.randomUUID().toString(),
                "topic-regional",
                consumer,
                100,
                15500,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }


}