package org.hifly.kafka.demo.consumer.string;

import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConsumerHandleTest {

    @Test
    public void testProcess() {
        List<String> actualRecords = new ArrayList<>();
        final ConsumerHandle<String, String> recordsHandler = new ConsumerHandle<>(actualRecords);
        recordsHandler.process(createConsumerRecords(), "group-1", "cons-1");
        actualRecords = recordsHandler.getValueStore();
        final List<String> expectedWords = Arrays.asList("test", "test2", "test3");
        assertThat(actualRecords, equalTo(expectedWords));
     
    }

    private ConsumerRecords<String, String> createConsumerRecords() {
        final String topic = "test";
        final int partition = 0;
        final TopicPartition topicPartition = new TopicPartition(topic, partition);
        final List<ConsumerRecord<String, String>> consumerRecordsList = new ArrayList<>();
        consumerRecordsList.add(new ConsumerRecord<>(topic, partition, 0, null, "test"));
        consumerRecordsList.add(new ConsumerRecord<>(topic, partition, 0, null, "test2"));
        consumerRecordsList.add(new ConsumerRecord<>(topic, partition, 0, null, "test3"));
        final Map<TopicPartition, List<ConsumerRecord<String, String>>> recordsMap = new HashMap<>();
        recordsMap.put(topicPartition, consumerRecordsList);
    
        return new ConsumerRecords<>(recordsMap);
      }
}  