package com.redhat.kafka.demo.producer.serializer.json;

import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import com.redhat.kafka.demo.producer.serializer.model.AuditItem;
import com.redhat.kafka.demo.producer.serializer.model.CustomData;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Date;
import java.util.Properties;

public class RunnerAuditItem {

    public static void main (String [] args) {
        JsonProducer<CustomData> jsonProducer = new JsonProducer<CustomData>();
        Properties properties = new Properties();
        properties.put("valueSerializer", "com.redhat.kafka.demo.producer.serializer.json.AuditItemJsonSerializer");
        jsonProducer.start(properties);
        bunchOfMessages("audit", jsonProducer);
    }

    public static void bunchOfMessages(String topic, JsonProducer jsonProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 0; i < 2; i++ ) {
            AuditItem auditItem = new AuditItem();
            auditItem.setMethod("test");
            lastRecord = jsonProducer.produceSync(new ProducerRecord<>(topic, auditItem));
        }
        RecordMetadataUtil.prettyPrinter(lastRecord);

    }

}
