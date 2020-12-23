package com.redhat.kafka.demo.orders.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.kafka.demo.orders.model.Order;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OrderJsonSerializer implements Serializer<Order> {

    private Logger log = LoggerFactory.getLogger(OrderJsonSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, Order data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception exception) {
            log.error("Error in serializing object {}", data, exception);
        }
        return retVal;

    }

    @Override
    public void close() {}

}