#!/usr/bin/env python3
import pika
import sys
import json
import random


def get_temperature(device_id: int) -> str:
    if device_id == 1:
        return random.randrange(10, 35)
    elif device_id == 2:
        return random.randrange(2, 23)
    else:
        return random.randrange(0, 40)


if len(sys.argv) != 3:
    print("Usage: " + sys.argv[0] + " <queueName> <count>")
    sys.exit(1)

queue  = sys.argv[1]
count = int(sys.argv[2])

print("count:\t%d\nqueue:\t%s" % (count, queue) )

msgBody = {
    "id" : 0 ,
    "body" :  "010101010101010101010101010101010101010101010101010101010101010101010"
}

connection = pika.BlockingConnection(pika.ConnectionParameters('localhost'))
channel = connection.channel()
channel.queue_declare(queue = queue)

properties = pika.BasicProperties(content_type='application/json', delivery_mode=1, priority=1, content_encoding='utf-8')
for i in range(count):
    msgBody["id"] = i
    msgBody["body"] = get_temperature(i)
    jsonStr = json.dumps(msgBody)
    properties.message_id = str(i)
    channel.basic_publish(exchange = '', routing_key = queue, body = jsonStr, properties = properties)
    print("Send\t%r" % msgBody)

connection.close()
print('Exiting')