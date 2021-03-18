import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;

import java.util.Random;

public class HazelcastServer4QueueProducer {
    public static void main(String[] args) {
        ClientConfig config = new ClientConfig();
        config.setInstanceName("inst2");
        HazelcastInstance client = HazelcastClient.newHazelcastClient(config);
        IQueue<Integer> queue = client.getQueue("queue");
        System.out.println("Queue size " + queue.size());

        new Thread(() -> {
            for (;;){
                try {
                    queue.put(new Random().nextInt(10));
                    Thread.sleep(1000);
                    System.out.println("Queue size " + queue.size());
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }).start();


    }
}
