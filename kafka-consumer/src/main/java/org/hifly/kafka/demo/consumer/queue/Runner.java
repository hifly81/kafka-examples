package org.hifly.kafka.demo.consumer.queue;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.hifly.kafka.demo.consumer.core.impl.AckConsumerHandle;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;
import org.hifly.kafka.demo.consumer.core.impl.SharedConsumerImpl;

import java.time.Duration;
import java.util.Properties;

public class Runner {

    public static void main (String [] args) throws Exception {
        String topics = "queue-topic";
        String groupId = "group-1";

        if(args != null && args.length >= 1) {
            topics = args[0];
        }
        if(args != null && args.length == 2) {
            groupId = args[1];
        }

        String BROKER_LIST =
                System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"localhost:9092,localhost:9093,localhost:9094";

        Properties properties = new Properties();
        properties.put("bootstrap.servers", BROKER_LIST);
        properties.put("group.id", groupId);
        properties.put("key.deserializer", StringDeserializer.class.getName());
        properties.put("value.deserializer", StringDeserializer.class.getName());

        SharedConsumerImpl<String, String> sharedConsumer =
                new SharedConsumerImpl<>(properties, new AckConsumerHandle<>());
        sharedConsumer.subscribe(groupId, topics, true);
        sharedConsumer.poll(Duration.ofMillis(100), -1, false);
    }
}
