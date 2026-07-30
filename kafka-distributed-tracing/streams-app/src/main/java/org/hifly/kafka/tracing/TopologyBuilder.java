package org.hifly.kafka.tracing;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopologyBuilder {

    private static final Logger log = LoggerFactory.getLogger(TopologyBuilder.class);

    public static final String INPUT_TOPIC  = "tracing-input";
    public static final String OUTPUT_TOPIC = "tracing-output";

    public static Topology build() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> inputStream = builder.stream(INPUT_TOPIC);

        inputStream
            .peek((key, value) ->
                log.info("Received message — key: {}, value: {}", key, value))
            .mapValues(value -> {
                String transformed = value.toUpperCase();
                log.info("Transformed value: {}", transformed);
                return transformed;
            })
            .filter((key, value) -> value != null && !value.isBlank())
            .peek((key, value) ->
                log.info("Sending to output — key: {}, value: {}", key, value))
            .to(OUTPUT_TOPIC);

        return builder.build();
    }
}