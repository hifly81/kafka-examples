package org.hifly.kafka.demo.producer.tx;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
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
            groupOfSynchMessages("test-idempotent", baseProducer);
            //Commit TX
            baseProducer.getProducer().commitTransaction();
        } catch (KafkaException e) {
            //Abort TX
            baseProducer.getProducer().abortTransaction();
        } finally {
            baseProducer.stop();
        }

    }

    public static void groupOfSynchMessages(String topic, StringTXProducer baseProducer) {
        RecordMetadata lastRecord;
        for (int i = 0; i < 10; i++ ) {
            for(int k =0; k < 1000; k++ ) {
                lastRecord = baseProducer.produceSync(new ProducerRecord<>(topic, "GROUP-" + i + "-" + k));
                RecordMetadataUtil.prettyPrinter(lastRecord);
            }
        }
    }

}
