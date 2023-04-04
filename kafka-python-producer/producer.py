from confluent_kafka import Producer


def ack(error, message):
    print("error={}".format(error))
    print("topic={}".format(message.topic()))
    print("timestamp={}".format(message.timestamp()))
    print("key={}".format(message.key()))
    print("value={}".format(message.value()))
    print("partition={}".format(message.partition()))
    print("offset={}".format(message.offset()))



conf = {'bootstrap.servers': "localhost:9092"}

producer = Producer(conf)

topic = 'test-python'
producer.produce(topic, key="1", value="Hello World", callback=ack)
producer.poll(1)
