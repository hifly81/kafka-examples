package org.hifly.kafka.demo.streams.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hifly.kafka.demo.streams.domain.CarInfo;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CarInfoSerializer implements Serializer<CarInfo> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarInfoSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, CarInfo data) {
        byte[] retVal = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            retVal = objectMapper.writeValueAsString(data).getBytes();
        } catch (Exception exception) {
            LOGGER.info("Error in serializing object"+ data);
        }
        return retVal;

    }

    @Override
    public void close() {}

}