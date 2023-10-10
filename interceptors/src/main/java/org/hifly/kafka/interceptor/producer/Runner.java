package org.hifly.kafka.interceptor.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hifly.kafka.demo.producer.RecordMetadataUtil;
import org.hifly.kafka.demo.producer.serializer.json.JsonProducer;

import java.util.Properties;

public class Runner {

    public static void main (String [] args) {
        JsonProducer<CreditCard> jsonProducer = new JsonProducer<>();
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CreditCardJsonSerializer.class.getName());
        properties.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, CreditCardProducerInterceptor.class.getName());
        KafkaProducer<String, CreditCard> kafkaProducer = new KafkaProducer<>(properties);
        jsonProducer.start(kafkaProducer);
        bunchOfMessages("test_custom_data", jsonProducer);
        jsonProducer.stop();
    }

    public static void bunchOfMessages(String topic, JsonProducer jsonProducer) {
        RecordMetadata lastRecord = null;
        CreditCard creditCard = new CreditCard();
        creditCard.setCreditCard("5434344FFFFF");
        lastRecord = jsonProducer.produceSync(new ProducerRecord<>(topic, creditCard));
        RecordMetadataUtil.prettyPrinter(lastRecord);
    }

}
