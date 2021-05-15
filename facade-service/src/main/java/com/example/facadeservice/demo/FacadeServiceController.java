package com.example.facadeservice.demo;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RestController
public class FacadeServiceController {
    private final ConnectionFactory factory = new ConnectionFactory();
    private Connection connection = null;
    private Channel channel = null;

    @Autowired
    private Environment environment;

    @Autowired
    private DiscoveryClient discoveryClient;

    @SneakyThrows
    @PostConstruct
    private void init() {
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDeclare(environment.getProperty(environment.getProperty("queue")),
                true, false, false, null);
    }

    @SneakyThrows
    @PostMapping("/facade")
    public Mono<Void> facade(@RequestBody String text) {
        Msg message = new Msg(UUID.randomUUID(), text);
        System.out.println(text);
        try {
            send(channel, text);
        } catch (Exception ignored) {
        }
        AtomicReference<URI> logInstance = getUriByServiceName("log");
        return WebClient.create(logInstance.get().toURL().toString())
                .post()
                .uri("/log")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(message), Msg.class)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @SneakyThrows
    @GetMapping("/facade")
    public Mono<String> facade() {
        AtomicReference<URI> messaging = getUriByServiceName("message");
        Mono<String> messages =  WebClient.create(messaging.get().toURL().toString())
                .get()
                .uri("/messages")
                .retrieve()
                .bodyToMono(String.class);

        Mono<String> logs = postToLoggingService();

        return messages.zipWith(logs, (msg, lg) -> msg + ": " + lg).onErrorReturn("Error");
    }

    @SneakyThrows
    private Mono<String> postToLoggingService() {
        AtomicReference<URI> messaging = getUriByServiceName("log");
        Mono<String> logs = WebClient.create(messaging.get().toURL().toString())
                .get()
                .uri("/log")
                .retrieve()
                .bodyToMono(String.class)
                .retry(10)
                .onErrorResume(throwable -> postToLoggingService())
                .timeout(Duration.ofMinutes(1));
        return logs;
    }

    private void send(Channel channel, String message) throws Exception {
        channel.basicPublish("", environment.getProperty("queue"), null, message.getBytes());
        System.out.println(" [x] Sent " + message);
    }

    private AtomicReference<URI> getUriByServiceName(String log) {
        AtomicReference<URI> logInstance = new AtomicReference<>();
        discoveryClient.getInstances(log)
                .stream()
                .findAny()
                .ifPresent(serviceInstance -> logInstance.set(serviceInstance.getUri()));
        return logInstance;
    }
}
