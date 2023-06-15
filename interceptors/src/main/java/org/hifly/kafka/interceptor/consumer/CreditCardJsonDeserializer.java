package org.hifly.kafka.interceptor.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.hifly.kafka.interceptor.producer.CreditCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class CreditCardJsonDeserializer implements Deserializer<CreditCard> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditCardJsonDeserializer.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {}

    @Override
    public CreditCard deserialize(String topic, byte[] data) {
        try {
            if (data == null){
                LOGGER.info("Null received at deserializing");
                return null;
            }
            return objectMapper.readValue(new String(data, "UTF-8"), CreditCard.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to MessageDto");
        }
    }


    @Override
    public void close() {}

}