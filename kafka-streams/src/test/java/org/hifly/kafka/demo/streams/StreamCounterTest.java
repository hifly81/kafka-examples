package org.hifly.kafka.demo.streams;

import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.hifly.kafka.demo.streams.serializer.CarInfoDeserializer;
import org.hifly.kafka.demo.streams.serializer.CarInfoSerializer;
import org.hifly.kafka.demo.streams.stream.CarBrandStream;
import org.hifly.kafka.demo.streams.stream.StreamCounter;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.apache.kafka.common.serialization.Serdes.String;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class StreamCounterTest {

    private String TOPIC_INPUT = "input-topic";
    private String TOPIC_OUTPUT = "topic-car-output";

    @Test
    public void stream() throws Exception {
        Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());


        StreamCounter streamCounter = new StreamCounter();

        Topology topology = streamCounter.createTopology(TOPIC_INPUT, TOPIC_OUTPUT);
        TopologyTestDriver testDriver = new TopologyTestDriver(topology, streamsProps);

        testDriver
            .createInputTopic(TOPIC_INPUT, String().serializer(), String().serializer())
            .pipeKeyValueList(this.prepareInput());

        final List<KeyValue<String, Long>> results =
            testDriver.createOutputTopic(TOPIC_OUTPUT, String().deserializer(), new LongDeserializer())
                    .readKeyValuesToList();

        assertThat(results, equalTo(expectedResults()));


        testDriver.close();
    }

    private List<KeyValue<String, String>>  prepareInput() {
        List<KeyValue<String, String>> values = Arrays.asList(
                new KeyValue<>("John", "transaction_1"),
                new KeyValue<>("Mark", "transaction_1"),
                new KeyValue<>("John", "transaction_2"));
        return values;
      }

      private List<KeyValue<String, Long>> expectedResults() {
          List<KeyValue<String, Long>> values = Arrays.asList(
                  new KeyValue<>("John", 1l),
                  new KeyValue<>("Mark", 1l),
                  new KeyValue<>("John", 2l));
          return values;
      }


}
