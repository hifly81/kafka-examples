#!/usr/bin/env bash

source include.sh

ZOOKEEPER_NODE="1"

mkdir -p /tmp/zk-bis-${ZOOKEEPER_NODE}
echo ${ZOOKEEPER_NODE} > /tmp/zk-bis-${ZOOKEEPER_NODE}/myid

${KAFKA_DIR}/bin/zookeeper-server-start.sh ${ZOO_CONFIG_DIR}/zookeeper-${ZOOKEEPER_NODE}.properties
