from confluent_kafka import Consumer

conf = {'bootstrap.servers': "localhost:9092", 'group.id': 'pythongroup', 'auto.offset.reset': 'earliest'}
topic = 'test-python'

consumer = Consumer(conf)

consumer.subscribe([topic])

while True:
    msg = consumer.poll(1.0)

    if msg is None:
        continue
    if msg.error():
        print("Error: {}".format(msg.error()))
        continue

    print('Message: {}'.format(msg.value().decode('utf-8')))

consumer.close()
