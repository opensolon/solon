package org.noear.solon.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.data.cache.CacheService;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Caffeine 封装的缓存服务
 *
 * @author noear
 * @since 1.9
 */
public class CaffeineCacheService implements CacheService {
    private String _cacheKeyHead;
    private int _defaultSeconds;

    private final Cache<String, Object> client;

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

        client = Caffeine.newBuilder()
                .expireAfterWrite(_defaultSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        client.put(key, obj);
    }

    @Override
    public void remove(String key) {
        client.put(key, null);
    }

    @Override
    public Object get(String key) {
        return client.getIfPresent(key);
    }

    @Override
    public <T> T getOrStore(String key, int seconds, Supplier supplier) {
        return (T) client.get(key, (k) -> supplier.get());
    }
}
