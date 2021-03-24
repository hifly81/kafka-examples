CREATE TABLE accounts (
    account_id VARCHAR PRIMARY KEY,
    fullname VARCHAR
) WITH (
    kafka_topic = 'accounts',
    partitions = 1,
    value_format = 'avro',
    VALUE_AVRO_SCHEMA_FULL_NAME = 'org.hifly.saga.payment.model.Account'
);

CREATE STREAM orders (
    account_id VARCHAR,
    amount DOUBLE,
    item_id VARCHAR,
    order_id VARCHAR,
    payee VARCHAR,
    typology VARCHAR,
    timestamp VARCHAR
) WITH (
    kafka_topic = 'orders',
    partitions = 1,
    value_format = 'avro',
    VALUE_AVRO_SCHEMA_FULL_NAME = 'org.hifly.saga.payment.model.Order',
    timestamp = 'timestamp',
    timestamp_format = 'yyyy-MM-dd HH:mm:ss'
);

CREATE TABLE orders_tx WITH (
    kafka_topic = 'order_actions',
    partitions = 1,
    key_format = 'avro',
    value_format = 'avro',
    VALUE_AVRO_SCHEMA_FULL_NAME = 'org.hifly.saga.payment.model.OrderAction')
AS
    SELECT 
           'TX_' + a.account_id + '_' + o.order_id as tx_id,
           CASE WHEN SUM(o.amount) < 0.0 THEN -1 ELSE 0 END AS tx_action,
           a.account_id AS account_id,
           as_value(a.account_id) as account,
           collect_list(o.item_id) as items,
           o.order_id AS order_id,
           as_value(o.order_id) as order
    FROM orders AS o
    LEFT JOIN accounts a ON o.account_id = a.account_id
    GROUP BY a.account_id, o.order_id
    EMIT CHANGES;

CREATE STREAM orders_tx_ack (
    tx_id VARCHAR,
    participiant_id VARCHAR,
    order_id VARCHAR,
    timestamp VARCHAR
) WITH (
    kafka_topic = 'order_actions_ack',
    partitions = 1,
    value_format = 'avro',
    VALUE_AVRO_SCHEMA_FULL_NAME = 'org.hifly.saga.payment.model.OrderActionAck',
    timestamp = 'timestamp',
    timestamp_format = 'yyyy-MM-dd HH:mm:ss'
);