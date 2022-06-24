package benchmark;

import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author noear 2022/6/24 created
 */
public class MapPutTest {
    private static Map<String, Object> cached = new ConcurrentHashMap<>();

    @Test
    public void test() {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 100000; i++) {
            get1("test");
        }

        System.out.println("times: " + (System.currentTimeMillis() - start));
    }

    private Object get1(String key) { //5
        Object val = cached.get(key);
        if (val == null) {
            synchronized (key.intern()) {
                val = cached.get(key);

                if (val == null) {
                    cached.put(key, key + ":1");
                }
            }
        }

        return val;
    }

    private Object get2(String key) { //48
        return cached.computeIfAbsent("test", k -> k + ":1");
    }
}
