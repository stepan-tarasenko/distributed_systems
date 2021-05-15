package com.example2.messagesservice.messagesservice;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

@RestController
public class MessagesServiceController {

    private final ArrayList<String> list = new ArrayList<>();
    private static final ConnectionFactory factory = new ConnectionFactory();

    @Autowired
    private Environment env;

    @PostConstruct
    private void init() {
        try {
            factory.setHost(env.getProperty("host"));
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.basicQos(1);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                list.add(message);
                System.out.println(" [x] Received '" + message + "'");
                System.out.println(" [x] Done, sending ack");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
            };
            channel.basicConsume(env.getProperty("queue"),
                    false, deliverCallback, consumerTag -> { });
        } catch (IOException | TimeoutException ignored) {
        }
    }

    @GetMapping("/messages")
    @ResponseBody
    public String message(){
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : list) {
            stringBuilder.append("{")
                    .append(",")
                    .append(s)
                    .append("}\n");
        }
        return stringBuilder.toString();
    }
}
