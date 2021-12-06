package org.hifly.kafka.demo.streams.processor;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class StreamUtils {

    private static final Logger logger = LoggerFactory.getLogger(StreamUtils.class);

    public static void createTopics(final Properties properties, List<NewTopic> topics) throws InterruptedException, ExecutionException, TimeoutException {
        try (final AdminClient client = AdminClient.create(properties)) {

            client.createTopics(topics).values().forEach( (topic, future) -> {
                try {
                    future.get();
                } catch (Exception ex) {
                }
            });

            Collection<String> topicNames = topics
                    .stream()
                    .map(NewTopic::name)
                    .collect(Collectors.toCollection(LinkedList::new));
            client
                    .describeTopics(topicNames)
                    .all()
                    .get(10, TimeUnit.SECONDS)
                    .forEach((name, description) -> logger.info("Topic Description: {}", description.toString()));
        }
    }
}
