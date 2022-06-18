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
    private Cache<String, Object> _data;

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

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        if (_defaultSeconds < 1) {
            _defaultSeconds = 30;
        }

        if (Utils.isEmpty(_cacheKeyHead)) {
            _cacheKeyHead = Solon.cfg().appName();
        }

        _data = Caffeine.newBuilder()
                .expireAfterWrite(_defaultSeconds, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        _data.put(key, obj);
    }

    @Override
    public void remove(String key) {
        _data.put(key, null);
    }

    @Override
    public Object get(String key) {
        return _data.getIfPresent(key);
    }

    @Override
    public <T> T getOrStore(String key, int seconds, Supplier supplier) {
        return (T) _data.get(key, (k) -> supplier.get());
    }
}
