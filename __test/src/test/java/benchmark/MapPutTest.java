/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package benchmark;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author noear 2022/6/24 created
 */
public class MapPutTest {
    private static Map<String, Object> cached = new ConcurrentHashMap<>();
    private static ReentrantLock SYNC_LOCK = new ReentrantLock();

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
            try {
                val = cached.get(key);

                if (val == null) {
                    cached.put(key, key + ":1");
                }
            } finally {
                SYNC_LOCK.unlock();
            }
        }

        return val;
    }

    private Object get2(String key) { //48
        return cached.computeIfAbsent("test", k -> k + ":1");
    }
}
