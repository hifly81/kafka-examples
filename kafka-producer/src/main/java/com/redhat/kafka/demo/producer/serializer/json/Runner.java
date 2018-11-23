package com.redhat.kafka.demo.producer.serializer.json;

import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

public class Runner {

    public static void main (String [] args) {
        JsonProducer<CustomData> jsonProducer = new JsonProducer<CustomData>();
        Properties properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.demo.producer.serializer.json.CustomDataJsonSerializer");
        jsonProducer.start(properties);
        bunchOfMessages("test_custom_data", jsonProducer);
        bunchOfFFMessages("test_custom_data", jsonProducer);
        bunchOfAsynchMessages("test_custom_data", jsonProducer);
    }

    public static void bunchOfMessages(String topic, JsonProducer jsonProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 30000; i++ )
            lastRecord = jsonProducer.produceSync(new ProducerRecord<>(topic, new CustomData(i)));
        RecordMetadataUtil.prettyPrinter(lastRecord);

    }

    public static void bunchOfFFMessages(String topic, JsonProducer baseProducer) {
        for (int i= 10; i < 30000; i++ )
            baseProducer.produceFireAndForget(new ProducerRecord<>(topic, new CustomData(i)));
    }

    public static void bunchOfAsynchMessages(String topic, JsonProducer baseProducer) {
        for (int i= 10; i < 30000; i++ )
            baseProducer.produceAsync(new ProducerRecord<>(topic, new CustomData(i)), new BaseProducerCallback());
    }
}
