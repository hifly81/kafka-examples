package org.hifly.kafka.order.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hifly.kafka.order.event.OrderEvent;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OrderEventJsonSerializer implements Serializer<OrderEvent> {

    private Logger log = LoggerFactory.getLogger(OrderEventJsonSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, OrderEvent data) {
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