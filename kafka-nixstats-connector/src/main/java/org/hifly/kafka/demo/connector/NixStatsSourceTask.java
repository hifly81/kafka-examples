package org.hifly.kafka.demo.connector;

import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.apache.kafka.connect.data.Schema;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class NixStatsSourceTask extends SourceTask {

    private String command;
    private String topic;
    private Long pollMs;
    private Long last_execution = 0L;
    private Long apiOffset;
    private String fromDate = "1970-01-01T00:00:00.0000000Z";

    private static final String COMMAND = "command";
    private static final String LAST_READ = "last_read";
    private static final String LAST_API_OFFSET = "last_api_offset";
    private static final Schema VALUE_SCHEMA = Schema.STRING_SCHEMA;

    @Override
    public String version() {
        return null;
    }

    @Override
    public void start(Map<String, String> map) {
        command = map.get(NixStatsSourceConnector.COMMAND_CONFIG);
        topic = map.get(NixStatsSourceConnector.TOPIC_CONFIG);
        pollMs = Long.valueOf(map.get(NixStatsSourceConnector.POLL_CONFIG));

        Map<String, Object> offset = context.offsetStorageReader().offset(Collections.singletonMap(COMMAND, command));
        if (offset != null) {
            Long lastRecordedOffset = (Long) offset.get("position");
            if (lastRecordedOffset != null)
                apiOffset = lastRecordedOffset;
        }
    }

    @Override
    public List<SourceRecord> poll() throws InterruptedException {

        if (System.currentTimeMillis() > (last_execution + pollMs)) {
            last_execution = System.currentTimeMillis();
            String result = execCommand(command);
            List<SourceRecord>  sourceRecords = new ArrayList<>();
            Map sourcePartition = Collections.singletonMap("filename", command);
            Map sourceOffset = Collections.singletonMap("position", ++apiOffset);
            sourceRecords.add(new SourceRecord(sourcePartition, sourceOffset, topic, Schema.STRING_SCHEMA, result));
            return sourceRecords;
        }
        return Collections.emptyList();
    }

    @Override
    public void stop() {

    }

    private String execCommand(String cmd) {
        String result = null;
        try (InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
             Scanner s = new Scanner(inputStream).useDelimiter("\\A")) {
             result = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
