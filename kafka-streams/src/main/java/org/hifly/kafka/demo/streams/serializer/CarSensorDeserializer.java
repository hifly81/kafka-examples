package org.hifly.kafka.demo.streams.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.hifly.kafka.demo.streams.CarSensor;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CarSensorDeserializer implements Deserializer<CarSensor> {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public CarSensor deserialize(String s, byte[] bytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String carSensor = new String(bytes, CHARSET);
            return objectMapper.readValue(carSensor, CarSensor.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error reading bytes! Yanlış", e);
        }
    }

    @Override
    public void close() {}

}