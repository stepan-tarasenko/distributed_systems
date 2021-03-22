package com.example1.loggingservice.loggingservice;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class LoggingServiceController {
    private final HazelcastInstance instance = Hazelcast.newHazelcastInstance();
    public static final String DISTR_MAP = "distr_map";

    @GetMapping("/log")
    public String getLogs() {
        IMap<UUID, String> map = instance.getMap(DISTR_MAP);
        System.out.println("map.toString(): " + map.toString());
        return "Map from LoggingService: " + map.toString();
    }

    @PostMapping("/log")
    public ResponseEntity<Void> log(@RequestBody Msg msg) {
        IMap<UUID, String> map = instance.getMap(DISTR_MAP);
        map.put(msg.getUuid(), msg.getText());
        System.out.println("map.put(): " + msg);
        return ResponseEntity.ok().build();
    }
}
