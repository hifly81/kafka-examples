package org.hifly.kafka.interceptor.producer;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CreditCardProducerInterceptor<K, V> implements ProducerInterceptor<K, V> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardProducerInterceptor.class);

    @Override
    public ProducerRecord<K, V> onSend(ProducerRecord<K, V> producerRecord) {
        CreditCard creditCard = (CreditCard) producerRecord.value();
        creditCard.setCreditCard("XXXXXX");
        LOGGER.info("record is:{}", producerRecord.value());
        return producerRecord;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}