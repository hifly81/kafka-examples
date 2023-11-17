package io.confluent.springboot.kafka.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class Controller {

    private int request = 0;

    @PostMapping(value="/api/message")
    public ResponseEntity send(@RequestBody String message) {
        System.out.println("\n\nRequest:" +  request);
        if(request < 2) {
            try {
                request++;
                System.out.println("Sleeping...");
                Thread.sleep(8000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Message:" +  message);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}