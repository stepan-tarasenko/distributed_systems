import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitmqProducer {
    public final static String QUEUE_NAME = "example_queue";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            String message = "Hello World!";
            for (int i = 0; i < 100; i++) {
                channel.basicPublish("", QUEUE_NAME, null, (message + i).getBytes());
                System.out.println(" [x] Sent '" + message + "'");
                Thread.sleep(1000);
            }
        }
    }
}
