#!/bin/bash

sh scripts/tear-down.sh

echo "download jmx exporter files..."
mkdir -p quotas/config/agent

wget -P quotas/config/agent https://raw.githubusercontent.com/confluentinc/jmx-monitoring-stacks/main/shared-assets/jmx-exporter/kafka_broker.yml
wget -P quotas/config/agent https://github.com/confluentinc/jmx-monitoring-stacks/blob/main/shared-assets/jmx-exporter/jmx_prometheus_javaagent-0.20.0.jar

echo "download grafana files..."
mkdir -p quotas/config/grafana/provisioning/dashboards
mkdir -p quotas/config/grafana/provisioning/datasources

wget -P quotas/config/grafana/provisioning/datasources https://raw.githubusercontent.com/confluentinc/jmx-monitoring-stacks/main/jmxexporter-prometheus-grafana/assets/grafana/provisioning/datasources/datasource.yml
wget -P quotas/config/grafana/provisioning/dashboards https://raw.githubusercontent.com/confluentinc/jmx-monitoring-stacks/main/jmxexporter-prometheus-grafana/assets/grafana/provisioning/dashboards/dashboard.yml
wget -P quotas/config/grafana/provisioning/dashboards https://raw.githubusercontent.com/confluentinc/jmx-monitoring-stacks/main/jmxexporter-prometheus-grafana/assets/grafana/provisioning/dashboards/kafka-quotas.json


echo "Starting Kafka cluster..."
docker-compose -f quotas/docker-compose.yml --env-file .env up -d
