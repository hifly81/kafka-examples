package org.hifly.kafka.demo.consumer.core;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerRecordUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerRecordUtil.class);

    public static void prettyPrinter(String groupId, String consumerId, ConsumerRecord consumerRecord) {
        if(consumerRecord != null) {
            System.out.printf("Group id %s - Consumer id: %s - Topic: %s - Partition: %s - Offset: %s - Key: %s - Value: %s\n",
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
