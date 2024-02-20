from socket import socket, AF_INET, SOCK_DGRAM
import time
import sys


host = "230.0.0.0"
port = 4446

UDPSock = socket(AF_INET, SOCK_DGRAM)

# run as py udp_stress_client.py 1000 10 to send 1000 bytes 10 times
args = sys.argv[1:]
message_size = int(args[0])
num_times = int(args[1])

# start the timer
timestamp = time.time_ns()

if not message_size or not num_times:
    print(
        "Error, you need to specify two numbers.  First the number of bytes to send, second the number of times to send them."
    )
    sys.exit(1)

print(f"Sending {message_size} bytes {num_times} times.")

# generate a message of message_size bytes
message = "X" * message_size
messages = message * num_times
size_in_bytes = len(messages.encode("utf-8"))
print(f"Message size: {size_in_bytes}")

for x in range(num_times):
    # try to send UDP message
    if not UDPSock.sendto(bytes(message, "utf-8"), (host, port)):
        print("UDP Send failed.")

UDPSock.close()

# stop the timer
donestamp = time.time_ns()
# print end of program with timestamp
print(f"Done at {donestamp}.")

# print execution time
print(f"Execution time: {donestamp - timestamp} in nano seconds.")
print(f"Execution time: {(donestamp - timestamp)/1000000} in milliseconds.")