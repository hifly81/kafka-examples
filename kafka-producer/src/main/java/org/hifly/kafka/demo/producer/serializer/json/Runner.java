package org.hifly.kafka.demo.producer.serializer.json;

import org.hifly.kafka.demo.producer.ProducerCallback;
import org.hifly.kafka.demo.producer.RecordMetadataUtil;
import org.hifly.kafka.demo.producer.serializer.model.CustomData;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    public static void main (String [] args) {
        JsonProducer<CustomData> jsonProducer = new JsonProducer<CustomData>("org.hifly.kafka.demo.producer.serializer.json.CustomDataJsonSerializer");
        jsonProducer.start();
        bunchOfMessages("test_custom_data", jsonProducer);
        jsonProducer.start();
        bunchOfFFMessages("test_custom_data", jsonProducer);
        jsonProducer.start();
        bunchOfAsynchMessages("test_custom_data", jsonProducer);
    }

    public static void bunchOfMessages(String topic, JsonProducer baseProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 0; i < 10; i++ ) {
            lastRecord = baseProducer.produceSync(new ProducerRecord<>(topic, new CustomData(i)));
            RecordMetadataUtil.prettyPrinter(lastRecord);
        }
        baseProducer.stop();

    }

    public static void bunchOfFFMessages(String topic, JsonProducer baseProducer) {
        for (int i= 0; i < 10; i++ )
            baseProducer.produceFireAndForget(new ProducerRecord<>(topic, new CustomData(i)));
        baseProducer.stop();
    }

    public static void bunchOfAsynchMessages(String topic, JsonProducer baseProducer) {
        for (int i= 0; i < 10; i++ )
            baseProducer.produceAsync(new ProducerRecord<>(topic, new CustomData(i)), new ProducerCallback());
        baseProducer.stop();
    }
}
