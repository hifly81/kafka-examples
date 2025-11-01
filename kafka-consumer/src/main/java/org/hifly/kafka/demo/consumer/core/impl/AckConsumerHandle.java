package org.hifly.kafka.demo.consumer.core.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.hifly.kafka.demo.consumer.core.AbstractConsumerHandle;
import org.hifly.kafka.demo.consumer.core.ConsumerRecordUtil;

import java.util.Map;

public class AckConsumerHandle<K,V> extends AbstractConsumerHandle<K,V> {

    public AckConsumerHandle() {}

    @Override
    public void addOffsets(Map<TopicPartition, OffsetAndMetadata> offsets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void process(ConsumerRecords<K, V> consumerRecords, String groupId, String consumerId) {
        for (ConsumerRecord<K, V> record : consumerRecords) {
            ConsumerRecordUtil.prettyPrinter(groupId,  consumerId, record);
        }
    }

    @Override
    public void process(ConsumerRecords<K, V> consumerRecords, String groupId) {
        this.process(consumerRecords, groupId, null);
    }

}
