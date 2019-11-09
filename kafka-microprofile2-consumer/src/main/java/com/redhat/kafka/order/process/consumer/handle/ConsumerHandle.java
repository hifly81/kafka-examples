package com.redhat.kafka.order.process.consumer.handle;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public abstract class ConsumerHandle {

    public abstract void process(ConsumerRecord record);
}
