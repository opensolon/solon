package org.noear.solon.cache.redisson;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.data.cache.CacheService;
import org.redisson.api.RedissonClient;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author noear
 * @since 1.7
 */
public class RedissonCacheService implements CacheService {
    protected String _cacheKeyHead;
    protected int _defaultSeconds;

    protected final RedissonClient client;

    public RedissonCacheService(RedissonClient client, String keyHeader, int defSeconds){
        this.client = client;

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        if (_defaultSeconds < 1) {
            _defaultSeconds = 30;
        }
    }

    public RedissonCacheService(Properties prop){
        this(prop, prop.getProperty("keyHeader"), 0);
    }

    public RedissonCacheService(Properties prop, String keyHeader, int defSeconds) {
        if (defSeconds == 0) {
            String defSeconds_str = prop.getProperty("defSeconds");

            if (Utils.isNotEmpty(defSeconds_str)) {
                defSeconds = Integer.parseInt(defSeconds_str);
            }
        }

        if(Utils.isEmpty(keyHeader)){
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

        client = RedissonBuilder.build(prop);
    }

    /**
     * 获取 RedisClient
     * */
    public RedissonClient client(){
        return client;
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if(obj == null){
            return;
        }

        if (seconds < 1) {
            seconds = _defaultSeconds;
        }

        String newKey = newKey(key);

        try {
            client.getBucket(newKey).set(obj, seconds, TimeUnit.SECONDS);
        } catch (Throwable e) {
            EventBus.pushTry(e);
        }
    }

    @Override
    public Object get(String key) {
        String newKey = newKey(key);

        return client.getBucket(newKey).get();
    }

    @Override
    public void remove(String key) {
        String newKey = newKey(key);

        client.getBucket(newKey).delete();
    }

    protected String newKey(String key) {
        return _cacheKeyHead + ":" + Utils.md5(key);
    }
}
