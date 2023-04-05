package org.hifly.kafka.demo.consumer.core;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumerRecordUtil {

    public static void prettyPrinter(String groupId, String consumerId, ConsumerRecord consumerRecord) {
        if(consumerRecord != null) {
            System.out.printf("Group id %s - Consumer id: %s - Topic: %s - Partition: %d - Offset: %d - Key: %s - Value: %s\n",
                    groupId,
                    consumerId,
                    consumerRecord.topic(),
                    consumerRecord.partition(),
                    consumerRecord.offset(),
                    consumerRecord.key(),
                    consumerRecord.value());
        }
    }
}
