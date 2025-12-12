#!/bin/bash

sh scripts/tear-down-cdc-mongo.sh


echo "Starting kafka docker containers..."
docker-compose -f cdc-debezium-mongo/docker-compose.yml --env-file .env up -d

echo "Wait 20 seconds..."
sleep 20

echo "Create roles for mongo CDC..."

docker exec -i mongo mongosh << EOF
use admin
db.createRole({
   role: "confluentCDCRole",
   privileges: [
      { resource: { cluster: true }, actions: ["find", "changeStream"] },
      { resource: { db: "outbox", collection: "Order" }, actions: [ "find", "changeStream" ] }
   ],
   roles: []
});

db.createUser({
  user: "data-platform-cdc",
  pwd: "password",
  roles: [
    { role: "read", db: "admin" },
    { role: "clusterMonitor", db: "admin" },
    { role: "read", db: "config" },
    { role: "read", db: "outbox" },
    { role: "confluentCDCRole", db: "admin"}
  ]
});

EOF

sleep 3

echo "Insert a record in outbox-->Order..."

docker exec -i mongo mongosh << EOF
use outbox
db.Order.insert([
{
  "orderId": "abc_003",
  "customerId": "mrc_001",
  "operation": "CREATE"
}
]);
EOF

echo "View record in outbox-->Order..."

docker exec -i mongo mongosh << EOF
use outbox
db.Order.find().pretty();
EOF

sleep 3

echo "Installing mongo debezium..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @cdc-debezium-mongo/config/debezium-source-mongo-transforms.json