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
			    log.Printf("connection request from %q to %q", dialConn.LocalAddr(), dialConn.RemoteAddr())
			}
			if err != nil {
				log.Fatalf("error establishing connection on bootstrap: %s", err)
			}

			log.Printf("connection started: client=%v destination=%v",
            		conn.RemoteAddr().String(),
            		dialConn.RemoteAddr().String())
            start := time.Now()

			go io.Copy(dialConn, conn)
			//  copy will block until it receives error or EOF (i.e. socket close)
			io.Copy(conn, dialConn)

			elapsed := time.Now().Sub(start)
			log.Printf("connection ended: client=%v destination=%v duration=%v",
                                    	conn.RemoteAddr().String(),
                                    	dialConn.RemoteAddr().String(),
                                    	elapsed.String())

			dialConn.Close()
			conn.Close()

		}()
	}
}



