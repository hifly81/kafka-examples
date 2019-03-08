package com.redhat.kafka.demo.streams.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.kafka.demo.streams.SpeedInfo;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.Charset;
import java.util.Map;

public class SpeedInfoDeserializer implements Deserializer<SpeedInfo> {

    private static final Charset CHARSET = Charset.forName("UTF-8");

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public SpeedInfo deserialize(String s, byte[] bytes) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String speedInfo = new String(bytes, CHARSET);
            return objectMapper.readValue(speedInfo, SpeedInfo.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error reading bytes! Yanlış", e);
        }
    }



    @Override
    public void close() {}

}