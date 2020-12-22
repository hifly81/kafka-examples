package com.redhat.kafka.order.process.consumer.offset;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.io.*;
import java.util.Map;
import java.util.Properties;

public class OffsetManager {

    public static Properties load() {
        Properties prop = null;
        InputStream input = null;
        try {
            input = new FileInputStream("/tmp/offsets.properties");
            prop = new Properties();
            prop.load(input);
        } catch (IOException ex) {
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }

    public static void store(Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {

            output = new FileOutputStream("/tmp/offsets.properties");
            for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsetAndMetadataMap.entrySet())
                prop.setProperty(entry.getKey().topic() + "-" + String.valueOf(entry.getKey().partition()), String.valueOf(entry.getValue().offset()));

            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}



