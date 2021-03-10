package com.example1.loggingservice.loggingservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class LoggingServiceController {
    private final Map<UUID, String> map = new HashMap<>();

    @GetMapping("/log")
    public String getLogs() {
        System.out.println(map.toString());
        return "Map from LoggingService: " + map.toString();
    }

    @PostMapping("/log")
    public ResponseEntity<Void> log(@RequestBody Msg msg) {
        System.out.println("Map from LoggingService: " + msg);
        map.put(msg.getUuid(), msg.getText());
        return ResponseEntity.ok().build();
    }
}
