package com.redhat.kafka.demo.producer.serializer.base;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.redhat.kafka.demo.producer.BaseProducerCallback;

import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class BaseProducerMockTest {

    @Test
    public void testProduce() throws IOException {
        BaseProducer baseProducer = new BaseProducer();

        final MockProducer<String, String> mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        baseProducer.start(mockProducer);

        final ProducerRecord<String, String> producerRecord1 = new ProducerRecord<>("topicOut", "foo", "bar");
        final ProducerRecord<String, String> producerRecord2 = new ProducerRecord<>("topicOut", null, "test");
        baseProducer.produceFireAndForget(producerRecord1);
        baseProducer.produceFireAndForget(producerRecord2);

        final List<KeyValue<String, String>> expectedList = Arrays.asList(KeyValue.pair("foo", "bar"),
            KeyValue.pair(null,"test"));

        final List<KeyValue<String, String>> actualList = mockProducer.history().stream().map(this::toKeyValue).collect(Collectors.toList());

        assertThat(actualList, equalTo(expectedList));
        
    }

    @Test
    public void testProduceAsync() throws IOException {
        BaseProducer baseProducer = new BaseProducer();

        final MockProducer<String, String> mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        baseProducer.start(mockProducer);

        final ProducerRecord<String, String> producerRecord1 = new ProducerRecord<>("topicOut", "foo", "bar");
        final ProducerRecord<String, String> producerRecord2 = new ProducerRecord<>("topicOut", null, "test");
        baseProducer.produceAsync(producerRecord1, new BaseProducerCallback());
        baseProducer.produceAsync(producerRecord2, new BaseProducerCallback());

        final List<KeyValue<String, String>> expectedList = Arrays.asList(KeyValue.pair("foo", "bar"),
            KeyValue.pair(null,"test"));

        final List<KeyValue<String, String>> actualList = mockProducer.history().stream().map(this::toKeyValue).collect(Collectors.toList());

        assertThat(actualList, equalTo(expectedList));
        
    }

    private KeyValue<String, String> toKeyValue(final ProducerRecord<String, String> producerRecord) {
        return KeyValue.pair(producerRecord.key(), producerRecord.value());
    }
}