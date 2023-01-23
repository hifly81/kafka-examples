package org.hifly.kafka.demo.producer.tx;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.hifly.kafka.demo.producer.ProducerCallback;
import org.hifly.kafka.demo.producer.RecordMetadataUtil;
import org.apache.kafka.common.KafkaException;

public class Runner {

    public static void main (String [] args) throws Exception {
        StringTXProducer baseProducer = new StringTXProducer();
        baseProducer.start();

        //Init TX
        baseProducer.getProducer().initTransactions();
        try {
            //Begin TX
            baseProducer.getProducer().beginTransaction();
            groupOfSynchMessages("topic1", baseProducer);
            //Commit TX
            baseProducer.getProducer().commitTransaction();
        } catch (KafkaException e) {
            //Abort TX
            baseProducer.getProducer().abortTransaction();
        }

    }

    public static void groupOfSynchMessages(String topic, StringTXProducer baseProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 30000; i++ )
            lastRecord = baseProducer.produceSync(new ProducerRecord<>(topic, "GROUP-1" + i));
        RecordMetadataUtil.prettyPrinter(lastRecord);
    }

    public static void bunchOfFFMessages(String topic, StringTXProducer baseProducer) {
        for (int i= 10; i < 30000; i++ )
             baseProducer.produceFireAndForget(new ProducerRecord<>(topic, Integer.toString(i)));
    }

    public static void bunchOfAsynchMessages(String topic, StringTXProducer baseProducer) {
        for (int i= 10; i < 30000; i++ )
            baseProducer.produceAsync(new ProducerRecord<>(topic, Integer.toString(i)), new ProducerCallback());
    }
}
