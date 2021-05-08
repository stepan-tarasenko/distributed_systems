package com.example1.loggingservice.loggingservice;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class LoggingServiceController {
    private final HazelcastInstance instance = Hazelcast.newHazelcastInstance();
    public static final String DISTR_MAP = "distr_map";

    @GetMapping("/log")
    public String getLogs() {
        IMap<UUID, String> map = instance.getMap(DISTR_MAP);
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<UUID, String> entry : map.entrySet()) {
            builder.append("[")
                    .append(entry.getKey())
                    .append(" ")
                    .append(entry.getValue())
                    .append("]")
                    .append("\n");
        }
        System.out.println("map.toString(): " + builder.toString());
        return "Map from LoggingService: " + builder.toString();
    }

    @PostMapping("/log")
    public ResponseEntity<Void> log(@RequestBody Msg msg) {
        IMap<UUID, String> map = instance.getMap(DISTR_MAP);
        map.put(msg.getUuid(), msg.getText());
        System.out.println("map.put(): " + msg);
        return ResponseEntity.ok().build();
    }
}
