package org.hifly.kafka.demo.streams.processor;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorSupplier;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class JSONArrayRemoveProcessorApplication {

    final static String INPUT_TOPIC = "input-topic";
    final static String OUTPUT_TOPIC = "output-topic";

    public static void main(String[] args) throws Exception {
        Properties streamsConfiguration = new Properties();
        if(args.length > 0) {
            InputStream input = new FileInputStream(args[0]);
            streamsConfiguration = new Properties();
            streamsConfiguration.load(input);
        } else {
            System.out.println("Read bootstrap from localhost...");
            streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        }

        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG, "logs_filtered_0");
        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();
        final Topology topology = builder.build();

        JSONArrayRemoveProcessor jsonArrayRemoveProcessor = new JSONArrayRemoveProcessor("id");

        topology.addSource("Source", INPUT_TOPIC)
                .addProcessor("Process", define(jsonArrayRemoveProcessor), "Source")
                .addSink("Sink", OUTPUT_TOPIC, Serdes.String().serializer(), Serdes.String().serializer(), "Process");


        final KafkaStreams streams = new KafkaStreams(topology, streamsConfiguration);
        streams.start();
    }

    private static <K, V> ProcessorSupplier<K, V> define(final Processor<K, V> processor) {
        return () -> processor;
    }

}
