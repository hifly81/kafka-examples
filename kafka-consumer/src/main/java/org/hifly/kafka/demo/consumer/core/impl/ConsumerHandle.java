package org.hifly.kafka.demo.consumer.core.impl;

import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.hifly.kafka.demo.consumer.core.AbstractConsumerHandle;
import org.hifly.kafka.demo.consumer.core.ConsumerRecordUtil;

public class ConsumerHandle<K,V> extends AbstractConsumerHandle<K,V> {

    private List<String> valueStore;
    private Map<TopicPartition, OffsetAndMetadata> offsets;

    public ConsumerHandle(List<String> valueStore) {
        this.valueStore = valueStore;
    }

    public List<String> getValueStore() {
        return valueStore;
    }

    public void addOffsets(Map<TopicPartition, OffsetAndMetadata> offsets) {
        this.offsets = offsets;
    }

    @Override
    public void process(ConsumerRecords<K, V> consumerRecords, String groupId, String consumerId) {
        for (ConsumerRecord<K, V> record : consumerRecords) {
            ConsumerRecordUtil.prettyPrinter(groupId,  consumerId, record);
            if(valueStore != null)
                valueStore.add(String.valueOf(record.value()));
            if(offsets != null)
                //store next offset to commit
                offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1, "null"));
        }
    }

    @Override
    public void pause(ConsumerRecords<K, V> consumerRecords, String groupId, String consumerId) {

    }

    @Override
    public void resume(ConsumerRecords<K, V> consumerRecords, String groupId, String consumerId) {

    }
}
