package org.hifly.kafka.interceptor.consumer;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.hifly.kafka.demo.consumer.core.ConsumerRecordUtil;
import org.hifly.kafka.interceptor.producer.CreditCard;

import java.util.Map;

public class CreditCardConsumerInterceptor<K, V> implements ConsumerInterceptor<K, V> {


    @Override
    public ConsumerRecords<K, V> onConsume(ConsumerRecords<K, V> consumerRecords) {
        for (ConsumerRecord<K, V> record : consumerRecords) {
            System.out.println("record headers:" + record.headers());
        }
        return consumerRecords;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}