package org.hifly.kafka.demo.producer.serializer.string;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.hifly.kafka.demo.producer.KafkaConfig;
import org.hifly.kafka.demo.producer.RecordMetadataUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    public static void main (String [] args) throws Exception {
        KafkaProducer<String, String> producer = new KafkaProducer<>(
                KafkaConfig.loadConfig("produce-kip-714.properties"));
        StringProducer baseProducer = new StringProducer();
        baseProducer.start(producer);
        bunchOfSynchMessages("topic1", baseProducer);
    }

    public static void bunchOfSynchMessages(String topic, StringProducer baseProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 0; i < 10; i++ ) {
            lastRecord = baseProducer.produceSync(new ProducerRecord<>(topic, Integer.toString(i)));
            RecordMetadataUtil.prettyPrinter(lastRecord);
        }
        baseProducer.stop();
    }

}
