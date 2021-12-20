package org.hifly.kafka.demo.consumer.offset;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

public class OffsetManager {

    private OffsetManager() {
        throw new IllegalStateException("Utility class");
    }

    public static Properties load() {
        Properties prop = null;
        String offsetPath = "/tmp/offsets.properties";
        File tempFile = new File(offsetPath);
        if(tempFile.exists()) {
            try (InputStream input = new FileInputStream("/tmp/offsets.properties");) {
                prop = new Properties();
                prop.load(input);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return prop;
    }

    public static void store(Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap) {
        Properties prop = new Properties();

        try(OutputStream output = new FileOutputStream("/tmp/offsets.properties");) {
            for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsetAndMetadataMap.entrySet())
                prop.setProperty(entry.getKey().topic() + "-" + entry.getKey().partition(), String.valueOf(entry.getValue().offset()));

            prop.store(output, null);

        } catch (IOException io) {
            io.printStackTrace();
        } 

    }
}



