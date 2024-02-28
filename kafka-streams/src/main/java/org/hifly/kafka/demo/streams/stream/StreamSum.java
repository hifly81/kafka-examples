package org.hifly.kafka.demo.streams.stream;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class StreamSum {

    private static final Logger logger = LoggerFactory.getLogger(StreamSum.class);

    private static final String BROKER_LIST =
            System.getenv("kafka.broker.list") != null ? System.getenv("kafka.broker.list") : "localhost:9092,localhost:9093,localhost:9094";

    public static void main (String [] args ) throws ExecutionException, InterruptedException, TimeoutException {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BROKER_LIST);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "streamsum_app");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        properties.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, "exactly_once_v2");
        properties.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 2);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(StreamsConfig.STATE_DIR_CONFIG, "/tmp/streams-streamsum");

        final String inputTopic = "sum-input-topic";
        final String outputTopic = "sum-output-topic";

        StreamSum streamCounter = new StreamSum();

        Topology topology = streamCounter.createTopology(inputTopic, outputTopic);
        logger.info(topology.describe().toString());

        KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
        final CountDownLatch latch = new CountDownLatch(1);

        // SIGTERM
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                kafkaStreams.close();
                latch.countDown();

            } catch (final Exception e) {}
        }));

        try {
            kafkaStreams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }

    public Topology createTopology(
            String inputTopic,
            String outputTopic) {

        StreamsBuilder builder = new StreamsBuilder();

        builder.stream(inputTopic, Consumed.as("StreamSum_input_topic").with(Serdes.String(), Serdes.String()))
                .peek((key, value) -> logger.info("Incoming record - key " +key +" value " + value))
                .mapValues(v -> Integer.valueOf(v))
                //groupByKey and sum values
                .groupByKey(Grouped.as("StreamSum_groupByKey").with(Serdes.String(), Serdes.Integer()))
                .reduce(Integer::sum, Named.as("StreamSum_sum"), Materialized.as("StreamSum-table"))
                //from ktable to kstream
                .toStream(Named.as("StreamSum_toStream"))
                .peek((key, value) -> logger.info("Outgoing record - key " +key +" value " + value))
                .to(outputTopic, Produced.as("StreamCounter_output_topic").with(Serdes.String(), Serdes.Integer()));

        return builder.build();

    }

}
