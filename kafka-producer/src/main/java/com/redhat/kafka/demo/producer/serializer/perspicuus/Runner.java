package com.redhat.kafka.demo.producer.serializer.perspicuus;

import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    public static void main (String [] args) {
        PerspicuusAvroDataProducer perspicuusAvroDataProducer = new PerspicuusAvroDataProducer();
        perspicuusAvroDataProducer.start();
        bunchOfMessages("test_perspicuus_data", perspicuusAvroDataProducer);
        bunchOfFFMessages("test_perspicuus_data", perspicuusAvroDataProducer);
        bunchOfAsynchMessages("test_perspicuus_data", perspicuusAvroDataProducer);
    }

    public static void bunchOfMessages(String topic, PerspicuusAvroDataProducer perspicuusAvroDataProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 30000; i++ ) {
            SpecificRecordBase specificRecordBase = new CarRecordBase();
            specificRecordBase.put("model", String.valueOf(i));
            specificRecordBase.put("brand", "The Best Car Company in Town");
            lastRecord = perspicuusAvroDataProducer.produceSync(new ProducerRecord<>(topic, specificRecordBase));
        }
        RecordMetadataUtil.prettyPrinter(lastRecord);

    }

    public static void bunchOfFFMessages(String topic, PerspicuusAvroDataProducer perspicuusAvroDataProducer) {
        for (int i= 10; i < 30000; i++ ) {
            SpecificRecordBase specificRecordBase = new CarRecordBase();
            specificRecordBase.put("model", String.valueOf(i));
            specificRecordBase.put("brand", "The Best Car Company in Town");
            perspicuusAvroDataProducer.produceFireAndForget(new ProducerRecord<>(topic, specificRecordBase));
        }
    }

    public static void bunchOfAsynchMessages(String topic, PerspicuusAvroDataProducer perspicuusAvroDataProducer) {
        for (int i= 10; i < 30000; i++ ) {
            SpecificRecordBase specificRecordBase = new CarRecordBase();
            specificRecordBase.put("model", String.valueOf(i));
            specificRecordBase.put("brand", "The Best Car Company in Town");
            perspicuusAvroDataProducer.produceAsync(new ProducerRecord<>(topic, specificRecordBase), new BaseProducerCallback());
        }
    }

}
