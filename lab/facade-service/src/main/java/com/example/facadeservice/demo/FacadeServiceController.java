package com.example.facadeservice.demo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class FacadeServiceController {
    private final WebClient logging = WebClient.create("http://localhost:8081/");
    private final WebClient messaging = WebClient.create("http://localhost:8082/");

    @PostMapping("/facade")
    public Mono<Void> facade(@RequestBody String text) {
        Msg message = new Msg(UUID.randomUUID(), text);
        System.out.println(text);
        return logging.post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(message), Msg.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @GetMapping("/facade")
    public Mono<String> facade() {
        Mono<String> messages =  messaging.get()
                                    .uri("/messages")
                                    .retrieve()
                                    .bodyToMono(String.class);

        Mono<String> logs = logging.get()
                                    .uri("/log")
                                    .retrieve()
                                    .bodyToMono(String.class);

        return messages.zipWith(logs, (msg, lg) -> msg + ": " + lg).onErrorReturn("Error");
    }
}
