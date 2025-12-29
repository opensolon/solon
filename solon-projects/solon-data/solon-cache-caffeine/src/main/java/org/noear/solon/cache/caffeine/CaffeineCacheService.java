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
package org.noear.solon.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.data.cache.CacheService;

import java.lang.reflect.Type;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Caffeine 封装的缓存服务
 *
 * @author noear
 * @since 1.9
 */
public class CaffeineCacheService implements CacheService {
    private final Cache<String, Object> client;
    private final ReentrantLock lock = new ReentrantLock();
    private MyExpiry expiry;
    private String _cacheKeyHead;
    private int _defaultSeconds;

    public CaffeineCacheService(Cache<String, Object> client, int defSeconds) {
        this(client, null, defSeconds);
    }

    public CaffeineCacheService(Cache<String, Object> client, String keyHeader, int defSeconds) {
        this.client = client;

        if (Utils.isEmpty(keyHeader)) {
            keyHeader = Solon.cfg().appName();
        }

        if (defSeconds < 1) {
            defSeconds = 30;
        }

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;
    }

    public CaffeineCacheService(Properties prop) {
        this(prop, prop.getProperty("keyHeader"), 0);
    }

    public CaffeineCacheService(Properties prop, String keyHeader, int defSeconds) {
        String defSeconds_str = prop.getProperty("defSeconds");

        if (defSeconds == 0) {
            if (Utils.isNotEmpty(defSeconds_str)) {
                defSeconds = Integer.parseInt(defSeconds_str);
            }
        }

        if (Utils.isEmpty(keyHeader)) {
            keyHeader = Solon.cfg().appName();
        }

        if (defSeconds < 1) {
            defSeconds = 30;
        }

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        expiry = new MyExpiry(_defaultSeconds);
        client = Caffeine.newBuilder().expireAfter(expiry).build();
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        lock.lock();
        try {
            expiry.setSupplier(() -> TimeUnit.SECONDS.toNanos(seconds));
            client.put(key, obj);
            expiry.setSupplier(null);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(String key) {
        client.invalidate(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key, Type type) {
        return (T) client.getIfPresent(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getOrStore(String key, Type type, int seconds, Supplier<T> supplier) {
        return (T) client.get(key, (k) -> supplier.get());
    }

    private static class MyExpiry implements Expiry<String, Object> {

        private final int defaultSeconds;
        private volatile Supplier<Long> customExpirySupplier;

        public MyExpiry(int seconds) {
            this.defaultSeconds = seconds;
        }

        public void setSupplier(Supplier<Long> supplier) {
            this.customExpirySupplier = supplier;
        }

        @Override
        public long expireAfterCreate(String key, Object value, long currentTime) {
            return getExpireNanos();
        }

        @Override
        public long expireAfterUpdate(
                String key, Object value, long currentTime, long currentDuration) {
            return getExpireNanos();
        }

        @Override
        public long expireAfterRead(String key, Object value, long currentTime, long currentDuration) {
            // 读取时保持原有剩余时间，不重新计算
            return currentDuration;
        }

        private long getExpireNanos() {
            Supplier<Long> supplier = customExpirySupplier;
            if (supplier != null) {
                return supplier.get();
            }
            return TimeUnit.SECONDS.toNanos(defaultSeconds);
        }
    }
}
