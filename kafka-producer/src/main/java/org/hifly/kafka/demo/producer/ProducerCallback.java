package org.hifly.kafka.demo.producer;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;

public class ProducerCallback implements Callback {
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null)
                e.printStackTrace();
            RecordMetadataUtil.prettyPrinter(recordMetadata);
        }
    }