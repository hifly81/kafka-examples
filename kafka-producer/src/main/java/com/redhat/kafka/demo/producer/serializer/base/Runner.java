package com.redhat.kafka.demo.producer.serializer.base;

import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    public static void main (String [] args) throws Exception {
        BaseProducer baseProducer = new BaseProducer();
        baseProducer.start(null);
        bunchOfSynchMessages("test", baseProducer);
        bunchOfFFMessages("test", baseProducer);
        bunchOfAsynchMessages("test", baseProducer);
    }

    public static void bunchOfSynchMessages(String topic, BaseProducer baseProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 30000; i++ )
            lastRecord = baseProducer.produceSync(new ProducerRecord<>(topic, Integer.toString(i)));
        RecordMetadataUtil.prettyPrinter(lastRecord);
    }

    public static void bunchOfFFMessages(String topic, BaseProducer baseProducer) {
        for (int i= 10; i < 30000; i++ )
             baseProducer.produceFireAndForget(new ProducerRecord<>(topic, Integer.toString(i)));
    }

    public static void bunchOfAsynchMessages(String topic, BaseProducer baseProducer) {
        for (int i= 10; i < 30000; i++ )
            baseProducer.produceAsync(new ProducerRecord<>(topic, Integer.toString(i)), new BaseProducerCallback());
    }
}
