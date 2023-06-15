package org.hifly.kafka.demo.streams.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.hifly.kafka.demo.streams.domain.CarSensor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CarSensorSerializer implements Serializer<CarSensor> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarSensorSerializer.class);

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public byte[] serialize(String topic, CarSensor data) {
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