package org.hifly.kafka.demo.producer.partitioner.custom;

import org.hifly.kafka.demo.producer.KafkaConfig;
import org.hifly.kafka.demo.producer.RecordMetadataUtil;
import org.hifly.kafka.demo.producer.serializer.string.StringProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Random;

public class Runner {

    private static final String[] strings = {
            "Mark",
            "Antony",
            "Paul"
    };

    public static void main (String [] args) {
        StringProducer baseProducer = new StringProducer();
        baseProducer.start(new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.stringProducerCustomPartitioner()));
        final String topicName = "demo-test";
        bunchOfSynchMessages(topicName, baseProducer);

    }

    public static void bunchOfSynchMessages(String topic, StringProducer baseProducer) {
        Random random = new Random();
        RecordMetadata lastRecord;
        for (int i= 10; i < 30000; i++ ) {
            String key = strings[random.nextInt(strings.length)];
            lastRecord = baseProducer.produceSync(new ProducerRecord<>(topic, key, Integer.toString(i)));
            System.out.printf("Key to send: %s\n", key);
            RecordMetadataUtil.prettyPrinter(lastRecord);
        }
    }


}
