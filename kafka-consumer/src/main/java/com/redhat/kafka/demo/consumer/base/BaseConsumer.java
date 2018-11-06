package com.redhat.kafka.demo.consumer.base;

import com.redhat.kafka.demo.consumer.BaseKafkaConsumer;
import com.redhat.kafka.demo.consumer.ConsumerRecordUtil;
import com.redhat.kafka.demo.consumer.KafkaConfig;
import com.redhat.kafka.demo.consumer.PartitionListener;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

public class BaseConsumer implements BaseKafkaConsumer {

    private Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private Consumer<String, String> consumer;
    private String id;
    private String groupId;
    private boolean autoCommit;

    public BaseConsumer(String id) {
        this.id = id;
    }

    @Override
    public void subscribe(String groupId, String topic, boolean autoCommit) {
        consumer = new KafkaConsumer<>(KafkaConfig.stringConsumer(groupId, autoCommit));
        consumer.subscribe(Collections.singletonList(topic), new PartitionListener(consumer, offsets));
        this.autoCommit = autoCommit;
        this.groupId = groupId;
    }

    @Override
    public void poll(int size) {
        if (consumer == null)
            throw new IllegalStateException("Can't poll, consumer not subscribed or null!");
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(size);
                for (ConsumerRecord<String, String> record : records) {
                    ConsumerRecordUtil.prettyPrinter(id, groupId, record);
                    //store next offset to commit
                    offsets.put(new TopicPartition(record.topic(), record.partition()), new
                            OffsetAndMetadata(record.offset() + 1, "null"));

                    //process record AND store in db ??
                }

                if (!autoCommit)
                    try {
                        //async doesn't do a retry
                        consumer.commitAsync(new OffsetCommitCallback() {
                            @Override
                            public void onComplete(Map<TopicPartition, OffsetAndMetadata> map, Exception e) {
                                if (e != null)
                                    e.printStackTrace();
                            }
                        });
                    } catch (CommitFailedException e) {
                        e.printStackTrace();
                    }

            }
        } finally {
            try {
                //sync does retries, we want to use it in case of last commit or rebalancing
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }

    }

    @Override
    public boolean assign(String topic, List partitions, boolean autoCommit) {
        boolean isAssigned = false;
        consumer = new KafkaConsumer<>(KafkaConfig.stringConsumer("", autoCommit));
        List<PartitionInfo> partitionsInfo = consumer.partitionsFor(topic);
        Collection<TopicPartition> partitionCollection = new ArrayList<>();
        if (partitionsInfo != null) {
            for (PartitionInfo partition : partitionsInfo) {
                if (partitions == null || partitions.contains(partition.partition()))
                    partitionCollection.add(new TopicPartition(partition.topic(), partition.partition()));
            }
            if (!partitionCollection.isEmpty()) {
                consumer.assign(partitionCollection);
                isAssigned = true;
            }
        }
        this.autoCommit = autoCommit;
        return isAssigned;
    }


}
