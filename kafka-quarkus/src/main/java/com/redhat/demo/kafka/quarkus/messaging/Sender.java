package com.redhat.demo.kafka.quarkus.messaging;

import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
public class Sender {

    @Inject
    Jsonb jsonb;

    private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);

    private AtomicLong messageCount = new AtomicLong(0);
    private BlockingQueue<Long> messages = new LinkedBlockingQueue<>();

    @Scheduled(every="1s")
    void schedule() {
        messages.add(messageCount.incrementAndGet());
    }


    @Outgoing("demo-prod")
    public CompletionStage<KafkaRecord<String, String>> send() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Long count = messages.take();
                LOGGER.info("Sending message to kafka with the message: " + count);
                String strCount = String.valueOf(count);
                return KafkaRecord.of("demo", strCount, strCount);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        });

    }

}


