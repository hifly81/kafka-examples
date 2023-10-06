package org.hifly.kafka.demo.consumer.retry;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.hifly.kafka.demo.consumer.core.ConsumerRecordUtil;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;
import org.hifly.kafka.demo.producer.ProducerCallback;
import org.hifly.kafka.demo.producer.serializer.string.StringProducer;

import java.math.BigInteger;
import java.util.*;

public class RetryHandle<K,V> extends ConsumerHandle<K,V> {

    private final String RETRY_TOPIC = "retry_topic";
    private final String DLQ_TOPIC = "dlq_topic";

    private Map<String, Integer> retriesPerKey = new HashMap<>();

    private int retries;

    private StringProducer stringProducer;

    public RetryHandle(List<String> valueStore, int retries) {
        super(valueStore);
        this.retries = retries;
        this.stringProducer = createProducer();
    }

    @Override
    public void process(ConsumerRecords<K, V> consumerRecords, String groupId, String consumerId) {
        for (ConsumerRecord<K, V> record : consumerRecords) {
            ConsumerRecordUtil.prettyPrinter(groupId, consumerId, record);
            for (Header recordHeader : record.headers()) {
                //This is only a sample way to detect if a record should be a candidate for retries. Real use-case will fail on same business logic
                if (recordHeader.key().equals("ERROR")) {
                    System.out.printf("Error message detected: number of retries left %s\n", retries);
                    if (retriesPerKey.containsKey(record.key())) {
                        Integer retryValue = retriesPerKey.get(record.key());
                        if (retryValue > 0) {
                            System.out.printf("send to RETRY topic: %s\n", RETRY_TOPIC);
                            //move to retry topic
                            this.stringProducer.produceAsync(
                                    (ProducerRecord<String, String>) new ProducerRecord<>(RETRY_TOPIC, record.key(), record.value()), new ProducerCallback());
                            retryValue = retryValue - 1;
                            retriesPerKey.put((String) record.key(), retryValue);
                        } else {
                            System.out.printf("number of retries exhausted, send to DLQ topic: %s\n", DLQ_TOPIC);
                            this.stringProducer.produceAsync(
                                    (ProducerRecord<String, String>) new ProducerRecord<>(DLQ_TOPIC, record.key(), record.value()), new ProducerCallback());
                        }
                    } else {
                        retriesPerKey.put((String) record.key(), retries);
                    }
                }

            }
        }
    }

    private StringProducer createProducer() {
        StringProducer stringProducer = new StringProducer();
        stringProducer.start();
        return stringProducer;
    }




}
