package com.redhat.kafka.order.process.consumer;

import com.redhat.kafka.order.process.consumer.handle.ConsumerHandle;
import com.redhat.kafka.order.process.consumer.offset.OffsetManager;
import com.redhat.kafka.order.process.consumer.partition.PartitionListener;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.util.*;

public class BaseConsumer<T> implements BaseKafkaConsumer {

    private Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
    private Consumer<String, T> consumer;
    private ConsumerHandle consumerHandle;
    private Properties properties;
    private String id;
    private String groupId;
    private boolean autoCommit;

    public BaseConsumer(String id, Properties properties, ConsumerHandle consumerHandle) {
        this.id = id;
        this.properties = properties;
        this.consumerHandle = consumerHandle;
    }

    @Override
    public void subscribe(String groupId, String topic, boolean autoCommit) {
        consumer = new KafkaConsumer<>(
                KafkaConfig.baseConsumerConfig(groupId, properties.getProperty("desererializerClass"), autoCommit));
        consumer.subscribe(Collections.singletonList(topic), new PartitionListener(consumer, offsets));
        this.autoCommit = autoCommit;
        this.groupId = groupId;
    }

    @Override
    public void poll(int size, long duration, boolean commitSync) {
        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.printf("Starting exit...\n");
            consumer.wakeup();
            try {
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        if (consumer == null)
            throw new IllegalStateException("Can't poll, consumer not subscribed or null!");

        try {
            if(duration == -1) {
                while (true) {
                    consume(size, commitSync);

                }
            } else {
                long startTime = System.currentTimeMillis();
                while(false||(System.currentTimeMillis()-startTime) < duration) {
                    consume(size, commitSync);
                }
            }
        }
        catch (WakeupException e) {
        // ignore for shutdown
        }
        finally {
            try {
                //print offsets
                //sync does retries, we want to use it in case of last commit or rebalancing
                consumer.commitSync();
                for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsets.entrySet())
                    System.out.printf("Consumer %s - partition %s - lastOffset %s\n", this.id, entry.getKey().partition(), entry.getValue().offset());
                //Store offsets
                OffsetManager.store(offsets);
            } finally {
                consumer.close();
            }
        }

    }

    @Override
    public boolean assign(String topic, List partitions, boolean autoCommit) {
        boolean isAssigned = false;
        consumer = new KafkaConsumer<>(
                KafkaConfig.baseConsumerConfig("", properties.getProperty("desererializerClass"), autoCommit));
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

    private void consume(int size, boolean commitSync) {
        ConsumerRecords<String, T> records = consumer.poll(size);
        for (ConsumerRecord<String, T> record : records) {
            ConsumerRecordUtil.prettyPrinter(id, groupId, record);
            //store next offset to commit
            offsets.put(new TopicPartition(record.topic(), record.partition()), new
                    OffsetAndMetadata(record.offset() + 1, "null"));
            consumerHandle.process(record);
        }

        if (!autoCommit)
            if(!commitSync) {
                try {
                    //async doesn't do a retry
                    consumer.commitAsync((map, e) -> {
                        if (e != null)
                            e.printStackTrace();
                    });
                } catch (CommitFailedException e) {
                    e.printStackTrace();
                }
            } else
                consumer.commitSync();
    }


}
