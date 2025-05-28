package org.hifly.kafka.demo.avro;

import org.apache.avro.Conversions.DecimalConversion;
import org.apache.kafka.clients.producer.*;
import org.hifly.kafka.demo.avro.domain.*;
import org.hifly.kafka.demo.avro.domain.cdc.*;
import org.hifly.kafka.demo.producer.AbstractKafkaProducer;
import org.hifly.kafka.demo.producer.IKafkaProducer;
import org.hifly.kafka.demo.producer.KafkaConfig;
import org.hifly.kafka.demo.producer.ProducerCallback;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.avro.Schema;
import org.apache.avro.LogicalTypes;


public class CDCProducer {

    private static final String TOPIC = "CDC_topic";

    private static final String schemaString6 = "{\n" +
            "  \"type\": \"bytes\",\n" +
            "  \"logicalType\": \"decimal\",\n" +
            "  \"precision\": 6,\n" +
            "  \"scale\": 0\n" +
            "}";

    private static final String schemaString3 = "{\n" +
            "  \"type\": \"bytes\",\n" +
            "  \"logicalType\": \"decimal\",\n" +
            "  \"precision\": 3,\n" +
            "  \"scale\": 0\n" +
            "}";

    private static final String schemaString8 = "{\n" +
            "  \"type\": \"bytes\",\n" +
            "  \"logicalType\": \"decimal\",\n" +
            "  \"precision\": 8,\n" +
            "  \"scale\": 0\n" +
            "}";

    private static final String schemaString5_2 = "{\n" +
            "  \"type\": \"bytes\",\n" +
            "  \"logicalType\": \"decimal\",\n" +
            "  \"precision\": 5,\n" +
            "  \"scale\": 2\n" +
            "}";

    public static void main (String [] args) {
        CDCKafkaProducer cdcKafkaProducer = new CDCKafkaProducer();
        cdcKafkaProducer.start();
        for(int i =0; i < 10; i++) {
            bunchOfMessages(TOPIC, cdcKafkaProducer);
        }
    }

    public static void bunchOfMessages(String topic, CDCKafkaProducer carProducer) {
        KeyRecord keyRecord = new KeyRecord();
        keyRecord.setFIELD1(createBuffer("155",schemaString3));
        keyRecord.setFIELD2(createBuffer("155",schemaString3));
        keyRecord.setFIELD3(createBuffer("12345678",schemaString8));
        keyRecord.setFIELD4(createBuffer("123456",schemaString6));

        DataRecord dataRecord = new DataRecord();
        Data data = new Data();
        data.setFIELD1(createBuffer("123",schemaString3));
        data.setFIELD2(createBuffer("155",schemaString3));
        data.setFIELD3(createBuffer("155.22",schemaString5_2));
        dataRecord.setData(data);

        Headers headers = new Headers();
        org.hifly.kafka.demo.avro.domain.cdc.operation op = operation.REFRESH;
        headers.setOperation(op);
        headers.setChangeSequence("");
        headers.setTimestamp("");
        headers.setStreamPosition("");
        headers.setTransactionId("");
        dataRecord.setHeaders(headers);

        carProducer.produceSync(new ProducerRecord<>(topic, keyRecord, dataRecord));

    }

    private static ByteBuffer createBuffer(String input, String schemaString) {

        Schema.Parser parser = new Schema.Parser();
        Schema decimalSchema = parser.parse(schemaString);

        LogicalTypes.Decimal decimalType = (LogicalTypes.Decimal) LogicalTypes.fromSchema(decimalSchema);
        DecimalConversion decimalConversion = new DecimalConversion();
        BigDecimal value = new BigDecimal(input);
        return decimalConversion.toBytes(value, decimalSchema, decimalType);
    }

    public static class CDCKafkaProducer extends AbstractKafkaProducer<KeyRecord, DataRecord> implements IKafkaProducer<KeyRecord, DataRecord> {

        private static final String BROKER_LIST =
                System.getenv("kafka.broker.list") != null? System.getenv("kafka.broker.list") :"pkc-03vj5.europe-west8.gcp.confluent.cloud:9092";
        private static final String CONFLUENT_SCHEMA_REGISTRY_URL =
                System.getenv("confluent.schema.registry") != null? System.getenv("confluent.schema.registry"):"https://psrc-knmwm.us-east-2.aws.confluent.cloud";

        @Override
        public void start() {
            Properties producerProperties = loadConfig("producer.properties");
            producer = new KafkaProducer(producerProperties);
        }

        @Override
        public void start(Producer<KeyRecord, DataRecord> kafkaProducer) {}

        @Override
        public void stop() {
            producer.close();
        }

        @Override
        public Future<RecordMetadata> produceFireAndForget(ProducerRecord<KeyRecord, DataRecord> producerRecord) {
            return producer.send(producerRecord);
        }

        @Override
        public void produceAsync(ProducerRecord<KeyRecord, DataRecord> producerRecord, Callback callback) {
            producer.send(producerRecord, new ProducerCallback());
        }

        @Override
        public RecordMetadata produceSync(ProducerRecord<KeyRecord, DataRecord> producerRecord) {
            RecordMetadata recordMetadata = null;
            try {
                recordMetadata = producer.send(producerRecord).get();
                System.out.println(recordMetadata);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return recordMetadata;
        }

        private static Properties loadConfig(final String configFile) {
            final Properties cfg = new Properties();

            ClassLoader classLoader = KafkaConfig.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(configFile);

            if (inputStream == null) {
                throw new IllegalArgumentException("file not found! " + configFile);
            } else {
                try {
                    cfg.load(inputStream);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return cfg;
        }
    }
}
