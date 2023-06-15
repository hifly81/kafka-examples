package org.hifly.kafka.demo.producer.partitioner.custom;


import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class UserPartitioner implements Partitioner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPartitioner.class);

    public void configure(Map<String, ?> configs) { }


    @Override
    public int partition(String topic, Object key, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();

        LOGGER.debug("Number of partitions: {}\n", numPartitions);

        if(numPartitions < 3)
            throw new IllegalStateException("not enough partitions!");

        if (( key).equals("Mark"))
            return 0;

        if (( key).equals("Antony"))
            return 1;

        if (( key).equals("Paul"))
            return 2;

        return 0;
    }

    public void close() { }
}