package com.example.facadeservice.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FacadeServiceApplication {
    
    @GetMapping("/actuator/health")
    public ResponseEntity<String> myCustomCheck() {
        String message = "Testing my health check function";
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    public static void main(String[] args) {
        SpringApplication.run(FacadeServiceApplication.class, args);
    }

}
