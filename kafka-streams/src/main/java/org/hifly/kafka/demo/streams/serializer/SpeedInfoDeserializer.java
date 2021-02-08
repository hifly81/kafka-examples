package org.hifly.kafka.demo.streams.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hifly.kafka.demo.streams.SpeedInfo;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SpeedInfoDeserializer implements Deserializer<SpeedInfo> {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

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