package first_point_to_point;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RabbitmqConsumer {

    private static Channel channel;
    public final static String CONSUMER_QUEUE_NAME = "consumer_queue";

    public static void main(String[] arguments) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.queueDelete(RabbitmqConsumer.CONSUMER_QUEUE_NAME);
        channel.queueDeclare(RabbitmqConsumer.CONSUMER_QUEUE_NAME, true,
                false, false, null);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println(" [x] Received '" + message + "'");

            try {
                doWork(message);
            } finally {
                System.out.println(" [x] Done, sending ack");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        channel.basicConsume(RabbitmqProducer.PRODUCER_QUEUE_NAME, false,
                deliverCallback, consumerTag -> { });
    }

    private static void doWork(String message) throws IOException {
        message += " modification from Consumer";
        channel.basicPublish("", CONSUMER_QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message);
    }
}
