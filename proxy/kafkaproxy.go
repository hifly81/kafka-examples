package main

import (
	"flag"
	"io"
	"log"
	"net"
)

var proxy *string = flag.String("proxy", "localhost:1999", "proxy address")
var bootstrap *string = flag.String("bootstrap", "broker:9092", "kafka bootstrap address")

func main() {
	flag.Parse()

	listener, err := net.Listen("tcp", *proxy)
	if err != nil {
		log.Fatalf("error creating proxy listener: %s", err)
	}
	for {
		conn, err := listener.Accept()
		if err != nil {
			log.Fatalf("error establishing connection: %s", err)
		}
		go func() {
			dialConn, err := net.Dial("tcp", *bootstrap)
			if err != nil {
				log.Fatalf("error establishing connection on bootstrap: %s", err)
			}
			go io.Copy(dialConn, conn)
			io.Copy(conn, dialConn)
			dialConn.Close()
			conn.Close()
		}()
	}
}