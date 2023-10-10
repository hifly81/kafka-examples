package org.hifly.kafka.interceptor.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.hifly.kafka.demo.consumer.core.ConsumerInstance;
import org.hifly.kafka.demo.consumer.core.KafkaConfig;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;
import org.hifly.kafka.interceptor.producer.CreditCard;

import java.io.IOException;
import java.util.UUID;

public class Runner {

    public static void main (String [] args) throws Exception {
        pollAutoCommit();
    }

    private static void pollAutoCommit() throws IOException {

        KafkaConsumer<String, CreditCard> consumer = new KafkaConsumer<>(
                KafkaConfig.loadConfig("consumer-interceptor.properties"));

        new ConsumerInstance<String , String>(
                UUID.randomUUID().toString(),
                "test_custom_data",
                consumer,
                100,
                -1,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }


}


