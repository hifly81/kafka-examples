package org.hifly.kafka.interceptor.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CreditCardJsonSerializer implements Serializer<CreditCard> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardJsonSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, CreditCard data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception exception) {
            LOGGER.error("Error in serializing object {}", data);
        }
        return retVal;

    }

    @Override
    public void close() {}

}