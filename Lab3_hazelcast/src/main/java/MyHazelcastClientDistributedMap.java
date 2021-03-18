import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.io.Serializable;
import java.util.Map;

public class MyHazelcastClientDistributedMap {
    public static void main(String[] args) {
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        HazelcastInstance client1 = HazelcastClient.newHazelcastClient();
        HazelcastInstance client2 = HazelcastClient.newHazelcastClient();
        HazelcastInstance client3 = HazelcastClient.newHazelcastClient();

        //Task 1
        Map<Integer, String> map1 = client.getMap("map1");
        for (int i = 0; i < 1000; i++) {
            map1.put(i, "val" + i);
        }

        //Task2 no lock
        new Thread(() -> {
            Map<String, Integer> map = client1.getMap("map");
            map.put("val", 0);
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.put("val", ++res);
            }
            System.out.println("Amount: " + client1.getMap("map").get("val"));
        }).start();
        new Thread(() -> {
            Map<String, Integer> map = client2.getMap("map");
            map.put("val", 0);
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.put("val", ++res);
            }
            System.out.println("Amount: " + client2.getMap("map").get("val"));
        }).start();
        new Thread(() -> {
            Map<String, Integer> map = client3.getMap("map");
            map.put("val", 0);
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.put("val", ++res);
            }
            System.out.println("Amount: " + client3.getMap("map").get("val"));
        }).start();

        //Task2 pessimistic lock
        new Thread(() -> {
            IMap<String, Integer> map = client1.getMap("map2");
            map.put("val", 0);
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.lock("val");
                try {
                    Thread.sleep( 10 );
                    map.put("val", ++res);
                } catch (InterruptedException ignored) {
                } finally {
                    map.unlock("val");
                }
            }
            System.out.println("Amount: " + client1.getMap("map2").get("val"));
        }).start();
        new Thread(() -> {
            IMap<String, Integer> map = client2.getMap("map2");
            map.put("val", 0);
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.lock("val");
                try {
                    Thread.sleep( 10 );
                    map.put("val", ++res);
                } catch (InterruptedException ignored) {
                } finally {
                    map.unlock("val");
                }
            }
            System.out.println("Amount: " + client2.getMap("map2").get("val"));
        }).start();
        new Thread(() -> {
            IMap<String, Integer> map = client3.getMap("map2");
            map.put("val", 0);
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.lock("val");
                try {
                    Thread.sleep( 10 );
                    map.put("val", ++res);
                } catch (InterruptedException ignored) {
                } finally {
                    map.unlock("val");
                }
            }
            System.out.println("Amount: " + client3.getMap("map2").get("val"));
        }).start();

        //Task2 optimistic lock
        new Thread(() -> {
            IMap<String, Value> map = client1.getMap("map3");
            map.put("val", new Value());
            for (int i = 0; i < 1000; i++) {
                try {
                    Value old = map.get("val");
                    Value neww = new Value(old);
                    neww.amount++;
                    Thread.sleep( 10 );
                    for (;;) {
                        if (map.replace("val", old, neww))
                            break;
                    }
                } catch (InterruptedException ignored) {
                }
            }
            System.out.println("Amount: " + ((Value)(client1.getMap("map3").get("val"))).amount);
        }).start();

        new Thread(() -> {
            IMap<String, Value> map = client2.getMap("map3");
            map.put("val", new Value());
            for (int i = 0; i < 1000; i++) {
                try {
                    Value old = map.get("val");
                    Value neww = new Value(old);
                    neww.amount++;
                    Thread.sleep( 10 );
                    for (;;) {
                        if (map.replace("val", old, neww))
                            break;
                    }
                } catch (InterruptedException ignored) {
                }
            }
            System.out.println("Amount: " + ((Value)(client2.getMap("map3").get("val"))).amount);
        }).start();
        new Thread(() -> {
            IMap<String, Value> map = client3.getMap("map3");
            map.put("val", new Value());
            for (int i = 0; i < 1000; i++) {
                try {
                    Value old = map.get("val");
                    Value neww = new Value(old);
                    neww.amount++;
                    Thread.sleep( 10 );
                    for (;;) {
                        if (map.replace("val", old, neww))
                            break;
                    }
                } catch (InterruptedException ignored) {
                }
            }
            System.out.println("Amount: " + ((Value)(client3.getMap("map3").get("val"))).amount);
        }).start();
    }

    static class Value implements Serializable {
        public int amount;

        public Value() {
        }

        public Value(Value that) {
            this.amount = that.amount;
        }

        public boolean equals( Object o ) {
            if ( o == this ) return true;
            if ( !( o instanceof Value ) ) return false;
            Value that = ( Value ) o;
            return that.amount == this.amount;
        }
    }
}
