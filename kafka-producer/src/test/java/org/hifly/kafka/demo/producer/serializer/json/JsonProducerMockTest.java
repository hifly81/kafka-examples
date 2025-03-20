package org.hifly.kafka.demo.producer.serializer.json;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.hifly.kafka.demo.producer.serializer.model.CustomData;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class JsonProducerMockTest {

    @Test
    public void testProduceFireAndForget() {
        JsonProducer<CustomData> jsonProducer = new JsonProducer<>();

        Partitioner partitioner = new Partitioner() {

            @Override
            public int partition(String s, Object o, byte[] bytes, Object o1, byte[] bytes1, Cluster cluster) {
                return 0;
            }

            @Override
            public void close() {
            }

            @Override
            public void configure(java.util.Map<String, ?> configs) {
            }
        };

        final MockProducer<String, CustomData> mockProducer =
                new MockProducer<>(true, partitioner, new StringSerializer(), new CustomDataJsonSerializer());
        jsonProducer.start(mockProducer);

        CustomData customData = new CustomData();
        customData.setIndex(1);

        final ProducerRecord<String, CustomData> producerRecord1 = new ProducerRecord<>("topicOut", "foo", customData);
        jsonProducer.produceFireAndForget(producerRecord1);

        final List<KeyValue<String, CustomData>> expectedList = Arrays.asList(KeyValue.pair("foo", customData));
        final List<KeyValue<String, CustomData>> actualList = mockProducer.history().stream().map(this::toKeyValue).collect(Collectors.toList());

        assertThat(actualList, equalTo(expectedList));
        
    }

    private KeyValue<String, CustomData> toKeyValue(final ProducerRecord<String, CustomData> producerRecord) {
        return KeyValue.pair(producerRecord.key(), producerRecord.value());
    }
}