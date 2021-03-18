import com.hazelcast.client.Client;
import com.hazelcast.client.ClientListener;
import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

public class HazelcastServer4QueueConsumer2 {
    public static void main(String[] args) {

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(getConfig());
        BlockingQueue<Integer> queue = instance.getQueue("queue");
        new Thread(() -> {
            for (;;) {
                try {
                    int take = queue.take();
                    System.out.println("Take " + take + " from queue");
                    Thread.sleep(1200);
                } catch (InterruptedException e) {
                    System.out.println(e.toString());
                }
            }
        }).start();
        System.out.println("Queue size=" + queue.size());

        instance.getClientService().addClientListener(new ClientListener() {
            @Override
            public void clientConnected(Client client) {
                System.out.println("Client connected, name=" + client.getName() + " uuid=" + client.getUuid());
            }

            @Override
            public void clientDisconnected(Client client) {
                System.out.println("Client disconnected, name=" + client.getName() + " uuid=" + client.getUuid());
            }
        });
    }

    private static Config getConfig() {
        Config config = new Config("inst2");
        HashMap<String, QueueConfig> configHashMap = new HashMap<>();
        QueueConfig queueConfig = new QueueConfig("queue");
        queueConfig.setMaxSize(10);
        configHashMap.put("queue", queueConfig);
        config.setQueueConfigs(configHashMap);
        config.getNetworkConfig().setPort(5703);
        return config;
    }
}
