package com.redhat.kafka.shipment.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.kafka.shipment.event.OrderEvent;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class OrderEventJsonDeserializer implements Deserializer<OrderEvent> {

    private ObjectMapper objectMapper;

    @Override
    public void configure(Map configs, boolean isKey) {
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public OrderEvent deserialize(String s, byte[] data) {
        try {
            return objectMapper.readValue(data, OrderEvent.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() { }
}