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

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.transforms.Transformation;
import org.apache.kafka.connect.transforms.util.NonEmptyListValidator;
import org.apache.kafka.connect.transforms.util.SimpleConfig;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.connect.data.Schema.*;


public class KeyFromFields<R extends ConnectRecord<R>> implements Transformation<R> {

    public static final String OVERVIEW_DOC = "Replace the record key with a new key formed from a concatenation of fields in the record value.    " +
            "It works with: " +
            "   \"key.converter\": \"org.apache.kafka.connect.storage.StringConverter\",\n" +
            "   \"value.converter\": \"org.apache.kafka.connect.storage.StringConverter\",\n" +
            "   \"key.converter.schemas.enable\": false,\n" +
            "   \"value.converter.schemas.enable\": false,";

    public static final String FIELDS_CONFIG = "fields";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(FIELDS_CONFIG, ConfigDef.Type.LIST, ConfigDef.NO_DEFAULT_VALUE, new NonEmptyListValidator(), ConfigDef.Importance.HIGH,
                    "Field names on the record value to extract as the record key.");

    private static final String PURPOSE = "copying and concat fields from value to key";

    private List<String> fields;

    private Map<String, Schema> schemaMapping = new HashMap<>() {{
        put(String.class.getName(), OPTIONAL_STRING_SCHEMA);
        put(Integer.class.getName(), OPTIONAL_INT32_SCHEMA);
        put("org.json.JSONObject$Null", OPTIONAL_STRING_SCHEMA);
    }};


    @Override
    public void configure(Map<String, ?> configs) {
        final SimpleConfig config = new SimpleConfig(CONFIG_DEF, configs);
        fields = config.getList(FIELDS_CONFIG);
    }

    @Override
    public R apply(R record) {
        return applySchemaless(record);
    }


    private R applySchemaless(R record) {

        //Get JSONObject from value
        JSONObject obj = new JSONObject(record.value().toString());

        //Create a composite key (concat of fields) with Schema as a String
        Schema schemaString = Schema.STRING_SCHEMA;
        StringBuilder keySb = new StringBuilder();
        for (String field : fields) {
            try {
                keySb.append(obj.get(field));
            } catch (Exception ex) {
                System.out.println("Can't parse:" + field + "-" + ex.getMessage());
            }
        }

        //Create schema for value
        SchemaBuilder schemaStruct = SchemaBuilder.struct();
        for (Object keyObj : obj.keySet()) {
            String key = (String) keyObj;
            //Inner json objects are treated as String
            if (obj.get(key) instanceof JSONObject) {
                schemaStruct.field(key, Schema.STRING_SCHEMA).build();
            } else {
                schemaStruct.field(key, schemaMapping.get(obj.get(key).getClass().getName())).build();
            }
        }

        //Create Struct for value
        Struct valueStruct = new Struct(schemaStruct.schema());
        for (Object keyObj : obj.keySet()) {
            String key = (String) keyObj;

            //Inner json objects are treated as String
            if (obj.get(key).getClass().getName().equalsIgnoreCase(JSONObject.class.getName())) {
                JSONObject innerObj = (JSONObject) obj.get(key);
                String innerValue = innerObj.toString();
                valueStruct.put(key, innerValue);

            } else {
                valueStruct.put(key, obj.get(key));
            }
        }

        return record.newRecord(record.topic(), record.kafkaPartition(), schemaString, keySb.toString(), schemaStruct.schema(), valueStruct, record.timestamp());
    }


    @Override
    public ConfigDef config() {
        return CONFIG_DEF;
    }

    @Override
    public void close() {
    }

}