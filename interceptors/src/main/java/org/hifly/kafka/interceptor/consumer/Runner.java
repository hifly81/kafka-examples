package org.hifly.kafka.interceptor.consumer;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.core.KafkaConfig;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;
import org.hifly.kafka.demo.consumer.core.ConsumerInstance;
import org.hifly.kafka.interceptor.producer.CreditCard;

import java.io.IOException;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

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
                500,
                true,
                false,
                true,
                new ConsumerHandle(null)).consume();
    }


}


