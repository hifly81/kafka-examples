package org.hifly.kafka.demo.orders.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hifly.kafka.demo.orders.model.Item;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;

public class ItemJsonDeserializer implements Deserializer<Item> {

    private ObjectMapper objectMapper;

    @Override
    public void configure(Map configs, boolean isKey) {
        this.objectMapper = new ObjectMapper();
    }


    @Override
    public Item deserialize(String s, byte[] data) {
        try {
            return objectMapper.readValue(data, Item.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() { }
}