package com.redhat.kafka.demo.producer.serializer.avro;

import com.redhat.kafka.demo.producer.BaseProducerCallback;
import com.redhat.kafka.demo.producer.RecordMetadataUtil;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    public static void main (String [] args) {
        AvroDataProducer avroDataProducer = new AvroDataProducer();
        avroDataProducer.start();
        bunchOfMessages("test_avro_data", avroDataProducer);
        bunchOfFFMessages("test_avro_data", avroDataProducer);
        bunchOfAsynchMessages("test_avro_data", avroDataProducer);
    }

    public static void bunchOfMessages(String topic, AvroDataProducer avroDataProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 30000; i++ ) {
            GenericRecord genericRecord = avroDataProducer.getGenericRecord();
            genericRecord.put("model", String.valueOf(i));
            genericRecord.put("brand", "The Best Car Company in Town");
            lastRecord = avroDataProducer.produceSync(new ProducerRecord<>(topic, genericRecord));
        }
        RecordMetadataUtil.prettyPrinter(lastRecord);

    }

    public static void bunchOfFFMessages(String topic, AvroDataProducer avroDataProducer) {
        for (int i= 10; i < 30000; i++ ) {
            GenericRecord genericRecord = avroDataProducer.getGenericRecord();
            genericRecord.put("model", String.valueOf(i));
            genericRecord.put("brand", "The Best Car Company in Town");
            avroDataProducer.produceFireAndForget(new ProducerRecord<>(topic, genericRecord));
        }
    }

    public static void bunchOfAsynchMessages(String topic, AvroDataProducer avroDataProducer) {
        for (int i= 10; i < 30000; i++ ) {
            GenericRecord genericRecord = avroDataProducer.getGenericRecord();
            genericRecord.put("model", String.valueOf(i));
            genericRecord.put("brand", "The Best Car Company in Town");
            avroDataProducer.produceAsync(new ProducerRecord<>(topic, genericRecord), new BaseProducerCallback());
        }
    }

}
