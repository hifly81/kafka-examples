# Produce messages
echo -e "\n# Produce messages to cars"
num_messages=1
(for i in `seq 1 $num_messages`; do echo "{\"model\":\"Dino\",\"brand\":\"Ferrari\",\"fuel_supply\":\"diesel\"}" ; done) | \
   kafka-avro-console-producer --topic cars \
                               --broker-list localhost:9092 \
                               --property value.schema.id=2 \
                               --property schema.registry.url=http://localhost:8081