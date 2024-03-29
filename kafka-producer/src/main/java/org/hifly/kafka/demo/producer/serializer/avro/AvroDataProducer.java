package org.hifly.kafka.demo.producer.serializer.avro;

import org.hifly.kafka.demo.producer.AbstractKafkaProducer;
import org.hifly.kafka.demo.producer.ProducerCallback;
import org.hifly.kafka.demo.producer.KafkaConfig;
import org.hifly.kafka.demo.producer.IKafkaProducer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.File;
import java.io.IOException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AvroDataProducer<K, Record> extends AbstractKafkaProducer<K, Record> implements IKafkaProducer<K, Record> {

    private Schema schema;
    private String avscSchema;
    private SchemaRegistry schemaRegistryEnumValue;
    private GenericRecord genericRecord;

    public AvroDataProducer(SchemaRegistry schemaRegistry) {
        this.schemaRegistryEnumValue = schemaRegistry;
    }

    public AvroDataProducer(SchemaRegistry schemaRegistry, String avscSchema) {
        this.schemaRegistryEnumValue = schemaRegistry;
        this.avscSchema = avscSchema;
    }

    public AvroDataProducer(SchemaRegistry schemaRegistry, Schema schema) {
        this.schemaRegistryEnumValue = schemaRegistry;
        this.schema = schema;
    }

    public void start() {
        switch (schemaRegistryEnumValue) {
            case CONFLUENT:
                producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.confluentAvroProducer());
                break;

            case APICURIO:
                producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.apicurioAvroProducer());
                break;

            case HORTONWORKS:
                producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.hortonworksAvroProducer());
                break;

            default:
                producer = new org.apache.kafka.clients.producer.KafkaProducer(KafkaConfig.confluentAvroProducer());
                break;
        }

        if(schema == null && (avscSchema != null && !avscSchema.equalsIgnoreCase(""))) {
            Schema.Parser parser = new Schema.Parser();
            try {
                ClassLoader classLoader = getClass().getClassLoader();
                schema = parser.parse(new File(classLoader.getResource(avscSchema).getFile()));
                genericRecord = new GenericData.Record(schema);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(Producer<K, Record> kafkaProducer) {
        producer = kafkaProducer;
    }

    public void stop() {
        producer.close();
    }

    public GenericRecord getGenericRecord() {
        if(genericRecord != null)
            return new GenericData.Record(schema);
        return genericRecord;
    }

    @Override
    public Future<RecordMetadata> produceFireAndForget(ProducerRecord<K, Record> producerRecord) {
        return producer.send(producerRecord);
    }

    @Override
    public RecordMetadata produceSync(ProducerRecord<K, Record> producerRecord) {
        RecordMetadata recordMetadata = null;
        try {
            recordMetadata = producer.send(producerRecord).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return recordMetadata;
    }

    @Override
    public void produceAsync(ProducerRecord<K, Record> producerRecord, Callback callback) {
        producer.send(producerRecord, new ProducerCallback());
    }

    public Schema getSchema() {
        return schema;
    }

}


