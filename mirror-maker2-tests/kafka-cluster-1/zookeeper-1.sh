#!/usr/bin/env bash

source include.sh

ZOOKEEPER_NODE="2"

mkdir -p /tmp/zk-${ZOOKEEPER_NODE}
echo ${ZOOKEEPER_NODE} > /tmp/zk-${ZOOKEEPER_NODE}/myid

${KAFKA_DIR}/bin/zookeeper-server-start.sh ${ZOO_CONFIG_DIR}/zookeeper-${ZOOKEEPER_NODE}.properties
