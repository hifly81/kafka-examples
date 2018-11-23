package com.redhat.kafka.order.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.kafka.demo.producer.serializer.json.CustomData;
import com.redhat.kafka.order.event.OrderEvent;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class OrderEventJsonSerializer implements Serializer<OrderEvent> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, OrderEvent data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception exception) {
            System.out.println("Error in serializing object"+ data);
        }
        return retVal;

    }

    @Override
    public void close() {}

}