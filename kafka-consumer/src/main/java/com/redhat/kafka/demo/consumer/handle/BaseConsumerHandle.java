package com.redhat.kafka.demo.consumer.handle;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class BaseConsumerHandle extends ConsumerHandle {

    @Override
    public void process(ConsumerRecord record) {
        //Do nothing
    }
}
