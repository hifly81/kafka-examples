package com.redhat.kafka.demo.consumer.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redhat.kafka.demo.consumer.BaseConsumer;
import com.redhat.kafka.demo.consumer.handle.BaseConsumerHandle;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BaseConsumerTest {

    @Test
    public void consumerTest() throws Exception {

        final String topic = "topic0";
        final TopicPartition topicPartition = new TopicPartition(topic, 0);
        final MockConsumer<String, String> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        List<String> actualRecords = new ArrayList<>();
        final BaseConsumerHandle<String, String> recordsHandler = new BaseConsumerHandle<>(actualRecords);

        BaseConsumer baseConsumer = new BaseConsumer(mockConsumer, "1", null, recordsHandler);

        // the KafkaConsumerApplication runs synchronously so the test needs to run
        // the application in its own thread
        new Thread(() -> {
            baseConsumer.subscribe("1", topic, true);
            baseConsumer.poll(1000, -1, true);
        }).start();
        Thread.sleep(500);
        addTopicPartitionsAssignmentAndAddConsumerRecords(topic, mockConsumer, topicPartition);
        Thread.sleep(500);
        baseConsumer.shutdown();

        actualRecords = recordsHandler.getValueStore();
        final List<String> expectedWords = Arrays.asList("test", "test2", "test3");
        assertThat(actualRecords, equalTo(expectedWords));
    }

    private void addTopicPartitionsAssignmentAndAddConsumerRecords(final String topic,
            final MockConsumer<String, String> mockConsumer, final TopicPartition topicPartition) {

        final Map<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(topicPartition, 0L);
        mockConsumer.rebalance(Collections.singletonList(topicPartition));
        mockConsumer.updateBeginningOffsets(beginningOffsets);
        addConsumerRecords(mockConsumer, topic);
    }

    private void addConsumerRecords(final MockConsumer<String, String> mockConsumer, final String topic) {
        mockConsumer.addRecord(new ConsumerRecord<>(topic, 0, 0, null, "test"));
        mockConsumer.addRecord(new ConsumerRecord<>(topic, 0, 1, null, "test2"));
        mockConsumer.addRecord(new ConsumerRecord<>(topic, 0, 2, null, "test3"));
    }

}
