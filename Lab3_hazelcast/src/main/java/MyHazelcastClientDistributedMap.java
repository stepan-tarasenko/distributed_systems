import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.io.Serializable;
import java.util.Map;

public class MyHazelcastClientDistributedMap {
    public static void main(String[] args) throws Exception{
        HazelcastInstance client = HazelcastClient.newHazelcastClient();
        HazelcastInstance client1 = HazelcastClient.newHazelcastClient();
        HazelcastInstance client2 = HazelcastClient.newHazelcastClient();
        HazelcastInstance client3 = HazelcastClient.newHazelcastClient();

        //Task 1
        Map<Integer, String> map1 = client.getMap("map1");
        for (int i = 0; i < 1000; i++) {
            map1.put(i, "val" + i);
        }
        Map<String, Integer> mp1 = client1.getMap("map");
        mp1.put("val", 0);
        IMap<String, Integer> mp2 = client1.getMap("map2");
        mp2.put("val", 0);
        IMap<String, Value> mp3 = client1.getMap("map3");
        mp3.put("val", new Value());
        //Task2 no lock
        Thread t1 = new Thread(() -> {
            Map<String, Integer> map = client1.getMap("map");
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.put("val", ++res);
            }
        });
        t1.start();
        Thread t2 = new Thread(() -> {
            Map<String, Integer> map = client2.getMap("map");
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.put("val", ++res);
            }
        });
        t2.start();
        Thread t3 = new Thread(() -> {
            Map<String, Integer> map = client3.getMap("map");
            for (int i = 0; i < 1000; i++) {
                int res = map.get("val");
                map.put("val", ++res);
            }
        });
        t3.start();
        //Task2 pessimistic lock
        Thread tt1 = new Thread(() -> {
            IMap<String, Integer> map = client1.getMap("map2");
            for (int i = 0; i < 1000; i++) {
                map.lock("val");
                int res = map.get("val");
                try {
                    Thread.sleep( 10 );
                    map.put("val", ++res);
                } catch (InterruptedException ignored) {
                } finally {
                    map.unlock("val");
                }
            }
        });
        tt1.start();
        Thread tt2 = new Thread(() -> {
            IMap<String, Integer> map = client2.getMap("map2");
            for (int i = 0; i < 1000; i++) {
                map.lock("val");
                int res = map.get("val");
                try {
                    Thread.sleep( 10 );
                    map.put("val", ++res);
                } catch (InterruptedException ignored) {
                } finally {
                    map.unlock("val");
                }
            }
        });
        tt2.start();
        Thread tt3 = new Thread(() -> {
            IMap<String, Integer> map = client3.getMap("map2");
            for (int i = 0; i < 1000; i++) {
                map.lock("val");
                int res = map.get("val");
                try {
                    Thread.sleep( 10 );
                    map.put("val", ++res);
                } catch (InterruptedException ignored) {
                } finally {
                    map.unlock("val");
                }
            }
        });
        tt3.start();

        //Task2 optimistic lock
        Thread ttt1 = new Thread(() -> {
            IMap<String, Value> map = client1.getMap("map3");
            for (int i = 0; i < 1000; i++) {
                try {
                    for (;;) {
                        Value old = map.get("val");
                        Value neww = new Value(old);
                        neww.amount++;
                        Thread.sleep( 10 );
                        if (map.replace("val", old, neww))
                            break;
                    }
                } catch (InterruptedException ignored) {
                }
            }
        });
        ttt1.start();

        Thread ttt2 = new Thread(() -> {
            IMap<String, Value> map = client2.getMap("map3");
            for (int i = 0; i < 1000; i++) {
                try {
                    for (;;) {
                        Value old = map.get("val");
                        Value neww = new Value(old);
                        neww.amount++;
                        Thread.sleep( 10 );
                        if (map.replace("val", old, neww))
                            break;
                    }
                } catch (InterruptedException ignored) {
                }
            }
        });
        ttt2.start();
        Thread ttt3 = new Thread(() -> {
            IMap<String, Value> map = client3.getMap("map3");
            for (int i = 0; i < 1000; i++) {
                try {
                    for (;;) {
                        Value old = map.get("val");
                        Value neww = new Value(old);
                        neww.amount++;
                        Thread.sleep( 10 );
                        if (map.replace("val", old, neww))
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        ttt3.start();

        t1.join();
        t2.join();
        t3.join();
        tt1.join();
        tt2.join();
        tt3.join();
        ttt1.join();
        ttt2.join();
        ttt3.join();

        System.out.println("No lock: " + mp1.get("val"));
        System.out.println("Pes lock: " + mp2.get("val"));
        System.out.println("Opt lock: " + mp3.get("val").amount);
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
