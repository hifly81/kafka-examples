package org.hifly.kafka.demo.consumer.string;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hifly.kafka.demo.consumer.core.impl.GenericConsumerImpl;
import org.hifly.kafka.demo.consumer.core.impl.ConsumerHandle;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicConsumerTest {

    @Test
    public void consumerTest() throws Exception {

        final String topic = "topic0";
        final TopicPartition topicPartition = new TopicPartition(topic, 0);
        final MockConsumer<String, String> mockConsumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
        List<String> actualRecords = new ArrayList<>();
        final ConsumerHandle<String, String> recordsHandler = new ConsumerHandle<>(actualRecords);

        GenericConsumerImpl<String, String> baseConsumer = new GenericConsumerImpl<>(mockConsumer, "1", null, recordsHandler);

        // the KafkaConsumerApplication runs synchronously so the test needs to run
        // the application in its own thread
        new Thread(() -> {
            baseConsumer.subscribe("1", topic, true);
            baseConsumer.poll(Duration.ofMillis(1000), -1, true);
        }).start();
        Thread.sleep(500);
        addTopicPartitionsAssignmentAndAddConsumerRecords(mockConsumer, topicPartition);
        Thread.sleep(500);
        baseConsumer.shutdown();

        actualRecords = recordsHandler.getValueStore();
        final List<String> expectedWords = Arrays.asList("test", "test2", "test3");
        assertThat(actualRecords, equalTo(expectedWords));
    }

    private void addTopicPartitionsAssignmentAndAddConsumerRecords(final MockConsumer<String, String> mockConsumer, final TopicPartition topicPartition) {

        final Map<TopicPartition, Long> beginningOffsets = new HashMap<>();
        beginningOffsets.put(topicPartition, 0L);
        mockConsumer.rebalance(Collections.singletonList(topicPartition));
        mockConsumer.updateBeginningOffsets(beginningOffsets);
        addConsumerRecords(mockConsumer);
    }

    private void addConsumerRecords(final MockConsumer<String, String> mockConsumer) {
        mockConsumer.addRecord(new ConsumerRecord<>("topic0", 0, 0, null, "test"));
        mockConsumer.addRecord(new ConsumerRecord<>("topic0", 0, 1, null, "test2"));
        mockConsumer.addRecord(new ConsumerRecord<>("topic0", 0, 2, null, "test3"));
    }

}
