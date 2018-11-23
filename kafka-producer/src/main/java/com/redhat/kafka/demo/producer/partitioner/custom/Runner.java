package com.redhat.kafka.demo.producer.partitioner.custom;

import com.redhat.kafka.demo.producer.KafkaConfig;
import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import com.redhat.kafka.demo.producer.serializer.base.BaseProducer;
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
        BaseProducer baseProducer = new BaseProducer();
        baseProducer.start(null, new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.stringProducerCustomPartitioner()));
        final String topicName = "demo-test";
        bunchOfSynchMessages(topicName, baseProducer);

    }

    public static void bunchOfSynchMessages(String topic, BaseProducer baseProducer) {
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
