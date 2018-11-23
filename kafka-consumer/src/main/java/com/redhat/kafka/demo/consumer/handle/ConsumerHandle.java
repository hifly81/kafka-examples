package com.redhat.kafka.demo.consumer.handle;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public abstract class ConsumerHandle {

    public abstract void process(ConsumerRecord record);
}
