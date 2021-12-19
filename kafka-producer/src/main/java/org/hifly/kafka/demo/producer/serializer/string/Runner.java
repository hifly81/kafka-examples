package org.hifly.kafka.demo.producer.serializer.string;

import org.hifly.kafka.demo.producer.ProducerCallback;
import org.hifly.kafka.demo.producer.RecordMetadataUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    public static void main (String [] args) throws Exception {
        StringProducer baseProducer = new StringProducer();
        baseProducer.start(null);
        bunchOfSynchMessages("topic1", baseProducer);
        bunchOfFFMessages("topic1", baseProducer);
        bunchOfAsynchMessages("topic1", baseProducer);
    }

    public static void bunchOfSynchMessages(String topic, StringProducer baseProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 30000; i++ )
            lastRecord = baseProducer.produceSync(new ProducerRecord<>(topic, Integer.toString(i)));
        RecordMetadataUtil.prettyPrinter(lastRecord);
    }

    public static void bunchOfFFMessages(String topic, StringProducer baseProducer) {
        for (int i= 10; i < 30000; i++ )
             baseProducer.produceFireAndForget(new ProducerRecord<>(topic, Integer.toString(i)));
    }

    public static void bunchOfAsynchMessages(String topic, StringProducer baseProducer) {
        for (int i= 10; i < 30000; i++ )
            baseProducer.produceAsync(new ProducerRecord<>(topic, Integer.toString(i)), new ProducerCallback());
    }
}
