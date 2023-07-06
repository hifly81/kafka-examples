package main

import (
	"flag"
	"io"
	"log"
	"net"
	"time"
)

func main() {

	proxy := flag.String("proxy", "localhost:1999", "proxy address")
	bootstrap := flag.String("bootstrap", "broker:9092", "kafka bootstrap address")

	flag.Parse()

	listener, err := net.Listen("tcp", *proxy)
	if err != nil {
		log.Fatalf("error creating proxy listener: %s", err)
	}
	defer listener.Close()

	host, port, err := net.SplitHostPort(listener.Addr().String())
	if err != nil {
		log.Fatalf("error get proxy details: %s", err)
	}

	log.Printf("[KAFKA PROXY] Listening on host: %s, port: %s\n", host, port)

	for {
		conn, err := listener.Accept()
		if err != nil {
			log.Fatalf("error establishing connection: %s", err)
		}
		go func() {
			dialConn, err := net.DialTimeout("tcp", *bootstrap, 10*time.Second)
			if err == nil {
			    log.Printf("[KAFKA PROXY] Connection request from %q to %q", dialConn.LocalAddr(), dialConn.RemoteAddr())
			    log.Printf("  |---> Connection success to %q", dialConn.RemoteAddr())
			}
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
