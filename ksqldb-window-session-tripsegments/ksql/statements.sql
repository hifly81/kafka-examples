CREATE STREAM vehicle_position (
    vehicle_id VARCHAR,
    latitude DOUBLE, 
    longitude DOUBLE, 
    timestamp VARCHAR
) WITH (
    kafka_topic = 'vehicle_position',
    timestamp='timestamp',
    timestamp_format='yyyy-MM-dd HH:mm:ss',
    partitions = 1,
    value_format = 'avro'
);


CREATE TABLE trips
    WITH (kafka_topic='trips') AS
    SELECT vehicle_id,
           COUNT(*) AS positions_sent,
           EARLIEST_BY_OFFSET(latitude) AS start_latitude,
           EARLIEST_BY_OFFSET(longitude) AS start_longitude,
           LATEST_BY_OFFSET(latitude) AS end_latitude,
           LATEST_BY_OFFSET(longitude) AS end_longitude,
           WINDOWSTART AS window_start,
           WINDOWEND AS window_end
    FROM vehicle_position
    WINDOW SESSION (5 MINUTES)
    GROUP BY vehicle_id;
