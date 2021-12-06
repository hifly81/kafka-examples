package org.hifly.kafka.demo.streams.processor;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class JSONArrayRemoveProcessorApplication {

    final static String INPUT_TOPIC = "processor-input-topic";
    final static String OUTPUT_TOPIC = "processor-output-topic";

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null ? System.getenv("kafka.broker.list") : "localhost:9092,localhost:9093,localhost:9094";

    public static void main(String[] args) throws Exception {
        Properties streamsConfiguration = new Properties();
        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "jsonprocessor_app");
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        List<NewTopic> topics = Arrays.asList(
                new NewTopic(INPUT_TOPIC, Optional.of(2), Optional.empty()),
                new NewTopic(OUTPUT_TOPIC, Optional.empty(), Optional.empty()));

        StreamUtils.createTopics(streamsConfiguration, topics);
        final Topology topology = builder.build();

        topology.addSource("Source", INPUT_TOPIC)
                .addProcessor("Process", new JSONArrayRemoveSupplier<>(), "Source")
                .addSink("Sink", OUTPUT_TOPIC, Serdes.String().serializer(), Serdes.String().serializer(), "Process");


        final KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);
        streams.start();
    }

    static class JSONArrayRemoveSupplier<KIn, VIn, KOut, VOut> implements ProcessorSupplier<String, String, String, String> {

        @Override
        public Processor<String, String, String, String> get() {
            return new JSONArrayRemoveProcessor("id");
        }
    }
}
