#!/usr/bin/env bash


start=0
end=3
increment=1

current=$start

while(( $(echo "$current < $end" | /usr/bin/bc -l) )); do
  /kafka_2.13-4.2.0/bin/trogdor.sh client createTask -t localhost:8889 -i node-$current --spec /tmp/trogdor/coordinator/compression/nocompression/node$current.json
  current=$(echo "$current + $increment" | /usr/bin/bc -l)
done
