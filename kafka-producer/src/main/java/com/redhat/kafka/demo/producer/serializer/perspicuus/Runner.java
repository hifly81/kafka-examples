package com.redhat.kafka.demo.producer.serializer.perspicuus;

import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    public static void main (String [] args) {
        AvroDataProducer avroDataProducer = new AvroDataProducer();
        avroDataProducer.start();
        bunchOfMessages("test_perspicuus_data", avroDataProducer);
        bunchOfFFMessages("test_perspicuus_data", avroDataProducer);
        bunchOfAsynchMessages("test_perspicuus_data", avroDataProducer);
    }

    public static void bunchOfMessages(String topic, AvroDataProducer avroDataProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 30000; i++ ) {
            SpecificRecordBase specificRecordBase = new CarRecordBase();
            specificRecordBase.put("model", String.valueOf(i));
            specificRecordBase.put("brand", "The Best Car Company in Town");
            lastRecord = avroDataProducer.produceSync(new ProducerRecord<>(topic, specificRecordBase));
        }
        RecordMetadataUtil.prettyPrinter(lastRecord);

    }

    public static void bunchOfFFMessages(String topic, AvroDataProducer avroDataProducer) {
        for (int i= 10; i < 30000; i++ ) {
            SpecificRecordBase specificRecordBase = new CarRecordBase();
            specificRecordBase.put("model", String.valueOf(i));
            specificRecordBase.put("brand", "The Best Car Company in Town");
            avroDataProducer.produceFireAndForget(new ProducerRecord<>(topic, specificRecordBase));
        }
    }

    public static void bunchOfAsynchMessages(String topic, AvroDataProducer avroDataProducer) {
        for (int i= 10; i < 30000; i++ ) {
            SpecificRecordBase specificRecordBase = new CarRecordBase();
            specificRecordBase.put("model", String.valueOf(i));
            specificRecordBase.put("brand", "The Best Car Company in Town");
            avroDataProducer.produceAsync(new ProducerRecord<>(topic, specificRecordBase), new BaseProducerCallback());
        }
    }

}
