package org.hifly.kafka.demo.consumer.retry;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.core.ConsumerInstance;

import java.util.Properties;
import java.util.UUID;

public class ConsumerRetries {

    private static final String TOPIC = "input-topic";

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092";

    public static void main (String [] args) {
        pollAutoCommit();
    }

    private static void pollAutoCommit() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        new ConsumerInstance<String , String>(
                "1",
                TOPIC,
                consumer,
                100,
                -1,
                true,
                false,
                true,
                new RetryHandle(null, 3)).consume();

    }
}


