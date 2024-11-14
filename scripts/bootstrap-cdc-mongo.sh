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
      { resource: { db: "outbox", collection: "loans" }, actions: [ "find", "changeStream" ] }
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

sleep 5

echo "Insert a record in outbox-->loans..."

docker exec -i mongo mongosh << EOF
use outbox
db.loans.insert([
{
  "aggregateId": "012313",
  "aggregateType": "Consumer Loan",
  "topicName": "CONSUMER_LOAN",
  "eventDate": "2024-08-20T09:42:02.665Z",
  "eventId": 1,
  "eventType": "INSTALLMENT_PAYMENT",
  "payload": {
    "amount": "200000"
  }
}
]);
EOF

echo "View record in outbox-->loans..."

docker exec -i mongo mongosh << EOF
use outbox
db.loans.find().pretty();
EOF

sleep 5

echo "Installing mongo debezium..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @cdc-debezium-mongo/config/debezium-source-mongo.json
echo "Installing mongo debezium with capture properties defined..."
curl -X POST -H Accept:application/json -H Content-Type:application/json http://localhost:8083/connectors/ -d @cdc-debezium-mongo/config/debezium-source-mongo-with-capture-scope-database.json