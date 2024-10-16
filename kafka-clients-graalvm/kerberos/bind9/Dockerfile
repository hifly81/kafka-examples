# Dockerfile for DNS (Bind9)
FROM debian:latest

RUN apt-get update && apt-get install -y bind9 && apt-get clean

# Expose the DNS ports
EXPOSE 53/udp 53/tcp

CMD ["named", "-g"]