import com.hazelcast.client.Client;
import com.hazelcast.client.ClientListener;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastServer1 {
    public static void main(String[] args) {

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(getConfig());
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
        Config config = new Config("inst1");
        config.getNetworkConfig().setPort(5701);
        return config;
    }
}
