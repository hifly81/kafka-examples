-- adding connectors
ADD JAR '/opt/sql-client/lib/flink-sql-connector-kafka-3.1.0-1.18.jar';
ADD JAR '/opt/sql-client/lib/flink-sql-avro-confluent-registry-1.18.1.jar';
ADD JAR '/opt/sql-client/lib/flink-json-1.18.1.jar';

DROP TABLE IF EXISTS heartbeat_60sec;
DROP TABLE IF EXISTS heartbeat;

CREATE TABLE heartbeat (
  person_id STRING,
  heartbeat_value INT,
  beat_time TIMESTAMP(3),
  WATERMARK FOR beat_time AS beat_time
) WITH (
      'connector' = 'kafka',
      'topic' = 'heartbeat',
      'properties.bootstrap.servers' = 'broker:9092',
      'properties.group.id' = 'flink-heartbeats',
      'scan.startup.mode' = 'earliest-offset',
      'key.format' = 'raw',
      'key.fields' = 'person_id',
      'value.format' = 'avro-confluent',
      'value.avro-confluent.url' = 'http://schema-registry:8081',
      'value.fields-include' = 'EXCEPT_KEY'
);

CREATE TABLE heartbeat_60sec (
  person_id STRING,
  window_start STRING,
  window_end STRING,
  heartbeats_over_120 BIGINT
) WITH (
  'connector' = 'kafka',
  'topic' = 'heartbeat_60sec',
  'properties.bootstrap.servers' = 'broker:9092',
  'properties.group.id' = 'flink-heartbeats60sec',
  'scan.startup.mode' = 'earliest-offset',
  'key.format' = 'raw',
  'key.fields' = 'person_id',
  'value.format' = 'avro-confluent',
  'value.avro-confluent.url' = 'http://schema-registry:8081',
  'value.fields-include' = 'EXCEPT_KEY'
);

INSERT INTO heartbeat_60sec
SELECT
  person_id,
  DATE_FORMAT(window_start, 'yyyy-MM-dd HH:mm:ss') AS window_start,
  DATE_FORMAT(window_end, 'yyyy-MM-dd HH:mm:ss') AS window_end,
  COUNT(*) AS heartbeats_over_120
FROM TABLE(TUMBLE(TABLE heartbeat, DESCRIPTOR(beat_time), INTERVAL '1' MINUTES))
WHERE
  heartbeat_value > 120
GROUP BY
  person_id, window_start, window_end;

INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 98, TO_TIMESTAMP('2023-02-18 15:10:00'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 97, TO_TIMESTAMP('2023-02-18 15:10:05'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 103, TO_TIMESTAMP('2023-02-18 15:10:10'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 102, TO_TIMESTAMP('2023-02-18 15:10:15'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 110, TO_TIMESTAMP('2023-02-18 15:10:20'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 122, TO_TIMESTAMP('2023-02-18 15:10:25'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 125, TO_TIMESTAMP('2023-02-18 15:10:30'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 121, TO_TIMESTAMP('2023-02-18 15:10:35'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 118, TO_TIMESTAMP('2023-02-18 15:10:40'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 98, TO_TIMESTAMP('2023-02-18 15:10:45'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 98, TO_TIMESTAMP('2023-02-18 15:10:50'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 93, TO_TIMESTAMP('2023-02-18 15:10:55'));

INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 111, TO_TIMESTAMP('2023-02-18 15:15:00'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 117, TO_TIMESTAMP('2023-02-18 15:15:05'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 125, TO_TIMESTAMP('2023-02-18 15:15:10'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 127, TO_TIMESTAMP('2023-02-18 15:15:15'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 128, TO_TIMESTAMP('2023-02-18 15:15:20'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 130, TO_TIMESTAMP('2023-02-18 15:15:25'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 131, TO_TIMESTAMP('2023-02-18 15:15:30'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 125, TO_TIMESTAMP('2023-02-18 15:15:35'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 127, TO_TIMESTAMP('2023-02-18 15:15:40'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 128, TO_TIMESTAMP('2023-02-18 15:15:45'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 129, TO_TIMESTAMP('2023-02-18 15:15:50'));
INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 128, TO_TIMESTAMP('2023-02-18 15:15:55'));

INSERT INTO heartbeat (person_id, heartbeat_value, beat_time) VALUES ('MGG1', 100, TO_TIMESTAMP('2023-02-18 15:30:00'));