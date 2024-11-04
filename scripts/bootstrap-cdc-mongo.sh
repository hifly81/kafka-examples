#!/bin/bash

sh scripts/tear-down-cdc-mongo.sh


echo "Starting kafka docker containers..."
docker-compose -f cdc-debezium-mongo/docker-compose.yml --env-file .env up -d

echo "Wait 30 seconds..."
sleep 30

echo "Create roles for mongo CDC..."

docker exec -i mongo mongosh << EOF
use admin
db.createRole({
   role: "confluentCDCRole",
   privileges: [
      { resource: { cluster: true }, actions: ["find", "changeStream"] },
      { resource: { db: "", collection: "" }, actions: [ "find", "changeStream" ] }
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
    { role: "read", db: "inventory" },
    { role: "confluentCDCRole", db: "admin"}
  ]
});

EOF

sleep 5

echo "Insert a record in inventory-->customers..."

docker exec -i mongo mongosh << EOF
use inventory
db.customers.insert([
{ _id : 1006, first_name : 'Bob', last_name : 'Hopper', email : 'thebob@example.com' }
]);
EOF

echo "View record in inventory-->customers..."

docker exec -i mongo mongosh << EOF
use inventory
db.customers.find().pretty();
EOF

sleep 5

echo "Installing mongo debezium..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @cdc-debezium-mongo/config/debezium-source-mongo.json
