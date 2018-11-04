package com.redhat.kafka.demo.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumerRecordUtil {

    public static void prettyPrinter(String threadId, ConsumerRecord consumerRecord) {
        if(consumerRecord != null) {
            System.out.printf("Thread id: %s - Topic: %s - Partition: %d - Offset: %d - Key: %s - Value: %s\n",
                    threadId,
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.offset(),
                    consumerRecord.key(),
                    consumerRecord.value());
        }
    }
}
