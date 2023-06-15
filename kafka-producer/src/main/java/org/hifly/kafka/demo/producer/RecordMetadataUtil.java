package org.hifly.kafka.demo.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordMetadataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecordMetadataUtil.class);

    public static void prettyPrinter(RecordMetadata recordMetadata) {
        if(recordMetadata != null) {
            LOGGER.info("Topic: {} - Partition: {} - Offset: {}\n",
                    recordMetadata.topic(),
                    recordMetadata.partition(),
                    recordMetadata.offset());
        }
    }
}
