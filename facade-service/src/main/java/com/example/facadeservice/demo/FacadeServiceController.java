package com.example.facadeservice.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

@RestController
public class FacadeServiceController {
    public final static String QUEUE = "queue";
    private final WebClient[] logging = {
            WebClient.create("http://localhost:8081/"),
            // WebClient.create("http://localhost:8083/"),
            // WebClient.create("http://localhost:8085/")
    };
    private final WebClient[] messaging = {
            WebClient.create("http://localhost:8085/"),
            WebClient.create("http://localhost:8082/"),
            WebClient.create("http://localhost:8089/")
    };
    private final Random random = new Random();
    private final ConnectionFactory factory = new ConnectionFactory();
    private Connection connection = null;
    private Channel channel = null;

    @SneakyThrows
    @PostConstruct
    private void init() {
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(QUEUE, true, false, false, null);
    }

    @PostMapping("/facade")
    public Mono<Void> facade(@RequestBody String text) {
        Msg message = new Msg(UUID.randomUUID(), text);
        System.out.println(text);
        try {
            send(channel, text);
        } catch (Exception ignored) {
        }
        return logging[random.nextInt(logging.length)]
                .post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(message), Msg.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @GetMapping("/facade")
    public Mono<String> facade() {
        Mono<String> messages =  messaging[random.nextInt(messaging.length)].get()
                                    .uri("/messages")
                                    .retrieve()
                                    .bodyToMono(String.class);


        Mono<String> logs = postToLoggingService();

        return messages.zipWith(logs, (msg, lg) -> msg + ": " + lg).onErrorReturn("Error");
    }

    private Mono<String> postToLoggingService() {
        Mono<String> logs = logging[random.nextInt(logging.length)]
                .get()
                .uri("/log")
                .retrieve()
                .bodyToMono(String.class)
                .retry(10)
                .onErrorResume(throwable -> postToLoggingService())
                .timeout(Duration.ofMinutes(1));
        return logs;
    }

    private static void send(Channel channel, String message) throws Exception {
        channel.basicPublish("", QUEUE, null, message.getBytes());
        System.out.println(" [x] Sent " + message);
    }
}
