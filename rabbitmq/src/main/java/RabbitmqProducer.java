import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class RabbitmqProducer {
    public final static String QUEUE_NAME = "example_queue";

    public static void main(String[] arg) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDelete(QUEUE_NAME);
            Map<String, Object> args = new HashMap<>();
            args.put("x-max-length", 5);
            args.put("x-overflow", "reject-publish");
            args.put("x-message-ttl", 60_000);
            channel.queueDeclare(QUEUE_NAME, true, false, false, args);
            String message = "msg";
            for (int i = 0; i < 10; i++) {
                channel.basicPublish("", QUEUE_NAME, null, (message + i).getBytes());
                System.out.println(" [x] Sent '" + (message + i) + "'");
                Thread.sleep(2000);
            }
        }
    }
}
