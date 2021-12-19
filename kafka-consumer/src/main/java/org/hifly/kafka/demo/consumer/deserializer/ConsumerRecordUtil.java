package org.hifly.kafka.demo.consumer.deserializer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumerRecordUtil {

    public static void prettyPrinter(String threadId, String groupId, ConsumerRecord consumerRecord) {
        if(consumerRecord != null) {
            System.out.printf("Thread id: %s - Group id %s - Topic: %s - Partition: %d - Offset: %d - Key: %s - Value: %s\n",
                    threadId,
                    groupId,
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.offset(),
                    consumerRecord.key(),
                    consumerRecord.value());
        }
    }
}
