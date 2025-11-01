package org.hifly.kafka.demo.consumer.partition;

import org.hifly.kafka.demo.consumer.offset.OffsetManager;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

public class PartitionListener<T> implements ConsumerRebalanceListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartitionListener.class);

    private final Consumer<String, T> consumer;
    private Map<TopicPartition, OffsetAndMetadata> offsets;

    public PartitionListener(Consumer<String, T> consumer, Map<TopicPartition, OffsetAndMetadata> offsets) {
        this.consumer = consumer;
        this.offsets = offsets;
    }

    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> collection) {

    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        Properties properties = OffsetManager.load();
        //seek from offset
        for (TopicPartition partition : partitions) {
            try {
                String offset = properties.getProperty(partition.topic() + "-" + partition.partition());
                if (offset != null) {
                    consumer.seek(partition, Long.parseLong(offset));
                    LOGGER.info("Consumer - partition {} - initOffset {}\n", partition.partition(), offset);
                }
            } catch (Exception ex) {
                //Ignore
            }

        }
    }

}
