package main

import (
	"flag"
	"io"
	"log"
	"net"
)

var localAddr *string = flag.String("local", "localhost:9999", "local address")
var bootstrap *string = flag.String("bootstrap", "broker:9092", "kafka bootstrap address")

func main() {
	flag.Parse()

	listener, err := net.Listen("tcp", *localAddr)
	if err != nil {
		panic(err)
	}
	for {
		conn, err := listener.Accept()
		if err != nil {
			log.Fatalf("error accepting connection", err)
			continue
		}
		go func() {
			conn2, err := net.Dial("tcp", *bootstrap)
			if err != nil {
				log.Println("error dialing remote addr", err)
				return
			}
			go io.Copy(conn2, conn)
			io.Copy(conn, conn2)
			conn2.Close()
			conn.Close()
		}()
	}
}