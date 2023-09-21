package org.noear.solon.cache.redisson;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.Serializer;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 1.7
 */
public class RedissonCacheService implements CacheService {
    static final Logger log = LoggerFactory.getLogger(RedissonCacheService.class);

    private String _cacheKeyHead;
    private int _defaultSeconds;
    private Serializer<String> _serializer = null;

    private final RedissonClient client;

    public RedissonCacheService serializer(Serializer<String> serializer) {
        if (serializer != null) {
            this._serializer = serializer;
        }

        return this;
    }

    public RedissonCacheService(RedissonClient client, int defSeconds) {
        this(client, null, defSeconds);
    }

    public RedissonCacheService(RedissonClient client, String keyHeader, int defSeconds) {
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

    public RedissonCacheService(Properties prop) {
        this(prop, prop.getProperty("keyHeader"), 0);
    }

    public RedissonCacheService(Properties prop, String keyHeader, int defSeconds) {
        if (defSeconds == 0) {
            String defSeconds_str = prop.getProperty("defSeconds");

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

        client = RedissonBuilder.build(prop);
    }

    /**
     * 获取 RedisClient
     */
    public RedissonClient client() {
        return client;
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if (obj == null) {
            return;
        }

        if (seconds < 1) {
            seconds = _defaultSeconds;
        }

        String newKey = newKey(key);

        try {
            if (_serializer == null) {
                client.getBucket(newKey).set(obj, seconds, TimeUnit.SECONDS);
            } else {
                obj = _serializer.serialize(obj); //序列化为 string
                client.getBucket(newKey, StringCodec.INSTANCE).set(obj, seconds, TimeUnit.SECONDS);
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public <T> T get(String key, Class<T> clz) {
        String newKey = newKey(key);


        try {
            if (_serializer == null) {
                return (T) client.getBucket(newKey).get();
            } else {
                Object obj = client.getBucket(newKey, StringCodec.INSTANCE).get();
                if (obj == null) {
                    return null;
                }

                obj = _serializer.deserialize((String) obj, clz);

                return (T) obj;
            }
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void remove(String key) {
        String newKey = newKey(key);

        if (_serializer == null) {
            client.getBucket(newKey).delete();
        } else {
            client.getBucket(newKey, StringCodec.INSTANCE).delete();
        }
    }

    protected String newKey(String key) {
        return _cacheKeyHead + ":" + Utils.md5(key);
    }
}
