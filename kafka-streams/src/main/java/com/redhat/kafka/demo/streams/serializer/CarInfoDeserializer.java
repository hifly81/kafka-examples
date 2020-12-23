package com.redhat.kafka.demo.streams.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.kafka.demo.streams.CarInfo;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CarInfoDeserializer implements Deserializer<CarInfo> {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public CarInfo deserialize(String s, byte[] bytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String carInfo = new String(bytes, CHARSET);
            return objectMapper.readValue(carInfo, CarInfo.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error reading bytes! Yanlış", e);
        }
    }

    @Override
    public void close() {}

}