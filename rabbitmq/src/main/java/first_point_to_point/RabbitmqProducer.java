package first_point_to_point;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitmqProducer {
    public final static String PRODUCER_QUEUE_NAME = "producer_queue";
    private static final ConnectionFactory factory = new ConnectionFactory();

    public static void main(String[] arguments) {
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println(" [x] Received modified message:'" + message + "'");
            };
            channel.basicConsume(RabbitmqConsumer.CONSUMER_QUEUE_NAME, false,
                    deliverCallback, consumerTag -> {});
        } catch (TimeoutException | IOException ignored) {
        }

        new Thread(() -> {
            try (Connection connection = factory.newConnection();
                 Channel channel = connection.createChannel()) {
                Map<String, Object> args = new HashMap<>();
                args.put("x-max-length", 100);
                args.put("x-message-ttl", 5_000);
                channel.queueDeclare(PRODUCER_QUEUE_NAME, true, false, false, args);
                sendMessages(channel);
            } catch (Exception ignored) {
            }
        }).start();
    }

    private static void sendMessages(Channel channel) throws Exception {
        String message = "Hello World!";
        for (int i = 0; i < 1_000; i++) {
            channel.basicPublish("", PRODUCER_QUEUE_NAME, null, (message + i).getBytes());
            System.out.println(" [x] Sent '" + (message + i) + "'");
            Thread.sleep(1000);
        }
    }
}
