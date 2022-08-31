#!/usr/bin/env bash

source include.sh

${KAFKA_DIR}/bin/kafka-server-start.sh ${BROKER_CONFIG_DIR}/server-0.properties
