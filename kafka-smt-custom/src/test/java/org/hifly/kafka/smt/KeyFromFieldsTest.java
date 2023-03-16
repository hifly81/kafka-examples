package org.hifly.kafka.smt;

import java.util.Collections;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.sink.SinkRecord;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class KeyFromFieldsTest {

    private final KeyFromFields<SinkRecord> xform = new KeyFromFields();

    @AfterEach
    public void teardown() {}

    @Test
    public void schemaless() {
        xform.configure(Collections.singletonMap("fields", "FIELD1,FIELD2,FIELD3"));
        String str = "{\n" +
                "  \"FIELD1\": \"01\",\n" +
                "  \"FIELD2\": \"20400\",\n" +
                "  \"FIELD3\": \"001\",\n" +
                "  \"FIELD4\": \"0006084655017\",\n" +
                "  \"FIELD5\": \"20221117\",\n" +
                "  \"FIELD6\": 9000018}";
        
        final SinkRecord record = new SinkRecord("", 0, null, null, null, str, 0);
        final SinkRecord transformedRecord = xform.apply(record);

        assertEquals(transformedRecord.keySchema(), Schema.STRING_SCHEMA);
        assertEquals(transformedRecord.key().toString(), "0120400001");

        System.out.println(transformedRecord.key());
        System.out.println(transformedRecord.value());

    }

}
