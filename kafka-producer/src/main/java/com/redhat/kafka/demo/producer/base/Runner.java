package com.redhat.kafka.demo.producer.base;

import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    public static void main (String [] args) throws Exception {
        BaseProducer baseProducer = new BaseProducer();
        baseProducer.start();
        bunchOfMessages("test", baseProducer);
    }

    public static void bunchOfMessages(String topic, BaseProducer baseProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 30000; i++ )
            lastRecord = baseProducer.produceSync(new ProducerRecord<>(topic, Integer.toString(i)));
        RecordMetadataUtil.prettyPrinter(lastRecord);

    }
}
