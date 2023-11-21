package org.hifly.demo.kafka.controller.kafka;

import org.springframework.kafka.support.SendResult;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.ArrayList;
import java.util.List;

@Service
public class Producer {

    @Value("${spring.kafka.topic.name}")
    private String TOPIC;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String message) {
        try {
            ListenableFuture<SendResult<String, String>> future = this.kafkaTemplate.send(this.TOPIC, message);
            future.addCallback(new ListenableFutureCallback<>() {
                @Override
                public void onSuccess(final SendResult<String, String> result) {
                    logger.info("sent: " + result + " with offset: " + result.getRecordMetadata().offset());
                }

                @Override
                public void onFailure(final Throwable throwable) {
                    logger.error("onFailure --> unable to send: " + message, throwable);
                }
            });
        } catch(Exception ex) {
            logger.error("unable to send: " + message, ex);
        }

    }


}