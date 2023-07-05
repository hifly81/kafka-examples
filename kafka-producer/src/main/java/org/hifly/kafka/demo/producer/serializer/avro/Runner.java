package org.hifly.kafka.demo.producer.serializer.avro;

import org.apache.avro.Schema;
import org.apache.kafka.common.record.Record;
import org.hifly.kafka.demo.producer.ProducerCallback;
import org.hifly.kafka.demo.producer.RecordMetadataUtil;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class Runner {

    private static final String TOPIC = "test_avro_data";
    private static final String MODEL = "model";
    private static final String BRAND = "brand";

    public static void main (String [] args) {

        //Get schema from args (CONFLUENT, APICURIO, HORTONWORKS)
        String schemaRegistry = args[0];

        //Schema from file
        AvroDataProducer<String, Record> avroDataProducer = new AvroDataProducer(SchemaRegistry.valueOf(schemaRegistry), "car.avsc");
        avroDataProducer.start();
        bunchOfMessages(TOPIC, avroDataProducer);
        bunchOfFFMessages(TOPIC, avroDataProducer);
        bunchOfAsynchMessages(TOPIC, avroDataProducer);

        //Schema from string
        String userSchema = "{\"type\":\"record\"," +
                "\"name\":\"myrecord\"," +
                "\"fields\":[{\"name\":\"f1\",\"type\":\"string\"}]}";

        Schema.Parser parser = new Schema.Parser();
        Schema schema = parser.parse(userSchema);

        avroDataProducer = new AvroDataProducer(SchemaRegistry.valueOf(schemaRegistry), schema);
        avroDataProducer.start();
        bunchOfMessages(TOPIC, avroDataProducer);
        bunchOfFFMessages(TOPIC, avroDataProducer);
        bunchOfAsynchMessages(TOPIC, avroDataProducer);
    }

    public static void bunchOfMessages(String topic, AvroDataProducer avroDataProducer) {
        RecordMetadata lastRecord = null;
        for (int i= 10; i < 100; i++ ) {
            GenericRecord genericRecord = avroDataProducer.getGenericRecord();
            genericRecord.put(MODEL, String.valueOf(i));
            genericRecord.put(BRAND, "The Best Car Company in Town");
            lastRecord = avroDataProducer.produceSync(new ProducerRecord<>(topic, genericRecord));
        }
        RecordMetadataUtil.prettyPrinter(lastRecord);

    }

    public static void bunchOfFFMessages(String topic, AvroDataProducer avroDataProducer) {
        for (int i= 10; i < 30000; i++ ) {
            GenericRecord genericRecord = avroDataProducer.getGenericRecord();
            genericRecord.put(MODEL, String.valueOf(i));
            genericRecord.put(BRAND, "The Best Car Company in Town2");
            avroDataProducer.produceFireAndForget(new ProducerRecord<>(topic, genericRecord));
        }
    }

    public static void bunchOfAsynchMessages(String topic, AvroDataProducer avroDataProducer) {
        for (int i= 10; i < 30000; i++ ) {
            GenericRecord genericRecord = avroDataProducer.getGenericRecord();
            genericRecord.put(MODEL, String.valueOf(i));
            genericRecord.put(BRAND, "The Best Car Company in Town");
            avroDataProducer.produceAsync(new ProducerRecord<>(topic, genericRecord), new ProducerCallback());
        }
    }

}
