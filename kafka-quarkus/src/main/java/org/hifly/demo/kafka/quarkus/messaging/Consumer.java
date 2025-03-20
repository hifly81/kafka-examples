package org.hifly.demo.kafka.quarkus.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class Consumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(Consumer.class);


    @Transactional
    @Incoming("demo")
    public CompletionStage<?> onMessage(KafkaRecord<String, String> message) {
        JsonNode json = null;
        try {
            json = objectMapper.readTree(message.getPayload());
            LOGGER.info("Received message from kafka with the message: " + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return message.ack();
    }

}
