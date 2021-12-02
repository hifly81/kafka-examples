package org.hifly.kafka.demo.streams;

import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.hifly.kafka.demo.streams.stream.StreamSum;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.apache.kafka.common.serialization.Serdes.String;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class StreamSumTest {

    private String TOPIC_INPUT = "input-topic";
    private String TOPIC_OUTPUT = "topic-car-output";

    @Test
    public void stream() {
        Properties streamsProps = new Properties();
        streamsProps.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamsProps.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());


        StreamSum streamSum = new StreamSum();

        Topology topology = streamSum.createTopology(TOPIC_INPUT, TOPIC_OUTPUT);
        TopologyTestDriver testDriver = new TopologyTestDriver(topology, streamsProps);

        testDriver
            .createInputTopic(TOPIC_INPUT, String().serializer(), new IntegerSerializer())
            .pipeKeyValueList(this.prepareInput());

        final List<KeyValue<String, Integer>> results =
            testDriver.createOutputTopic(TOPIC_OUTPUT, String().deserializer(), new IntegerDeserializer())
                    .readKeyValuesToList();

        assertThat(results, equalTo(expectedResults()));


        testDriver.close();
    }

    private List<KeyValue<String, Integer>>  prepareInput() {
        List<KeyValue<String, Integer>> values = Arrays.asList(
                new KeyValue<>("John", 1),
                new KeyValue<>("Mark", 2),
                new KeyValue<>("John", 5));
        return values;
      }

      private List<KeyValue<String, Integer>> expectedResults() {
          List<KeyValue<String, Integer>> values = Arrays.asList(
                  new KeyValue<>("John", 1),
                  new KeyValue<>("Mark", 2),
                  new KeyValue<>("John", 6));
          return values;
      }


}
