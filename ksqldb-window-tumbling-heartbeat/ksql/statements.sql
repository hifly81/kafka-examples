CREATE STREAM heartbeat (
    person_id VARCHAR,
    heartbeat_value DOUBLE, 
    timestamp VARCHAR
) WITH (
    kafka_topic = 'heartbeat',
    timestamp='timestamp',
    timestamp_format='yyyy-MM-dd HH:mm:ss',
    partitions = 1,
    value_format = 'avro'
);


CREATE TABLE heartbeat_60sec
    WITH (kafka_topic='heartbeat_60sec') AS
    SELECT person_id,
           COUNT(*) AS beat_over_threshold_count,
           WINDOWSTART AS window_start,
           WINDOWEND AS window_end
    FROM heartbeat
    WINDOW TUMBLING (SIZE 1 MINUTES)
    where heartbeat_value > 120
    GROUP BY person_id;
