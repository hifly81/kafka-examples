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
        xform.configure(Collections.singletonMap("fields", "C_IST,C_AG,PRGR_GGL_RAPP"));
        String str = "{\n" +
                "  \"C_IST\": \"01\",\n" +
                "  \"C_AG\": \"20400\",\n" +
                "  \"C_TP_RAPP\": \"001\",\n" +
                "  \"N_RAPP\": \"0006084655017\",\n" +
                "  \"DT_CNT\": \"20221117\",\n" +
                "  \"PRGR_GGL_RAPP\": 9000018,\n" +
                "   \"C_MATR\": null,\n" +
                "   \"NT\": { \"string\": \"N-/FRACCT/0449960801\" },\n" +
                "   \"CTV_DV_FN\": { \"long\": 7000 }}";
        
        final SinkRecord record = new SinkRecord("", 0, null, null, null, str, 0);
        final SinkRecord transformedRecord = xform.apply(record);

        assertEquals(transformedRecord.keySchema(), Schema.STRING_SCHEMA);
        assertEquals(transformedRecord.key().toString(), "01204009000018");

        System.out.println(transformedRecord.key());
        System.out.println(transformedRecord.value());

    }

}
