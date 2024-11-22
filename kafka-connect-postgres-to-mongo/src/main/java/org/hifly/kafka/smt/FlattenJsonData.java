/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hifly.kafka.smt;

import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.data.SchemaBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class FlattenJsonData implements Transformation<ConnectRecord> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ConnectRecord apply(ConnectRecord record) {
        // Get the value of the record (it should be a Struct in this case)
        if (record.value() == null) {
            return record;
        }

        Struct value = (Struct) record.value();

        // Get the json_data field from the record
        String jsonData = value.getString("json_data");
        if (jsonData == null) {
            return record;  // Return the record unchanged if json_data is null
        }

        // Parse json_data into a Map
        Map<String, Object> parsedJson;
        try {
            parsedJson = objectMapper.readValue(jsonData, Map.class);
        } catch (Exception e) {
            throw new ConnectException("Error parsing json_data field", e);
        }

        // Create a new Struct to include the original fields and flattened json fields
        Struct newValue = new Struct(value.schema());

        // Add original fields
        newValue.put("ssn", value.getString("ssn"));
        newValue.put("fullname", value.getString("fullname"));

        // Flatten the json_data and add its fields
        for (Map.Entry<String, Object> entry : parsedJson.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            // Handle nested objects
            if (fieldValue instanceof Map) {
                // Convert nested objects to structs
                newValue.put(fieldName, new Struct(SchemaBuilder.struct().field("type", Schema.STRING_SCHEMA).build()).put("type", fieldValue));
            } else {
                newValue.put(fieldName, fieldValue);
            }
        }

        // Return the transformed record with the new value
        return record.newRecord(record.topic(), record.kafkaPartition(), record.keySchema(), record.key(), newValue.schema(), newValue, record.timestamp());
    }

    @Override
    public ConfigDef config() {
        return new ConfigDef();
    }

    @Override
    public void close() {
        // Nothing to close for this example
    }

    @Override
    public void configure(Map<String, ?> configs) {
        // No configuration needed for this example
    }
}