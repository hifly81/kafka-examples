package org.hifly.kafka.demo.consumer.deserializer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumerRecordUtil {

    public static void prettyPrinter(String groupId, ConsumerRecord consumerRecord) {
        if(consumerRecord != null) {
            System.out.printf("Group id %s - Topic: %s - Partition: %d - Offset: %d - Key: %s - Value: %s\n",
                    groupId,
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.offset(),
                    consumerRecord.key(),
                    consumerRecord.value());
        }
    }
}
