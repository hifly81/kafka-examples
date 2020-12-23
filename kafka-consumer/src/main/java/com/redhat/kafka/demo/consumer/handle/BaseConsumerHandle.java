package com.redhat.kafka.demo.consumer.handle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.redhat.kafka.demo.consumer.ConsumerRecordUtil;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

public class BaseConsumerHandle<K,V> extends ConsumerHandle<K,V> {

    private Map<TopicPartition, OffsetAndMetadata> offsets;

    public void addOffsets(Map<TopicPartition, OffsetAndMetadata> offsets) {
        this.offsets = offsets;
    }

    @Override
    public List<String> process(ConsumerRecords<K, V> consumerRecords) {
        List<String> result = new ArrayList<>();
        for (ConsumerRecord<K, V> record : consumerRecords) {
            ConsumerRecordUtil.prettyPrinter(null, null, record);
            result.add(String.valueOf(record.value()));
            if(offsets != null)
                //store next offset to commit
                offsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1, "null"));
        }
        return result;
    }
}
