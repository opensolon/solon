package org.noear.solon.cache.jedis;

import org.noear.redisx.RedisClient;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.Serializer;

import java.util.Properties;

/**
 * Redis 封装的缓存服务
 *
 * @author noear
 * @since 1.3
 */
public class RedisCacheService implements CacheService {
    protected String _cacheKeyHead;
    protected int _defaultSeconds;
    protected final RedisClient _redisClient;
    private Serializer<String> _serializer = null;

    public RedisCacheService serializer(Serializer<String> serializer) {
        if(serializer != null) {
            this._serializer = serializer;
        }

        return this;
    }

    public RedisCacheService(Properties prop) {
        this(prop, prop.getProperty("keyHeader"), 0);
    }

    public RedisCacheService(Properties prop, String keyHeader, int defSeconds) {
        String defSeconds_str = prop.getProperty("defSeconds");
        String db_str = prop.getProperty("db");
        String maxTotal_str = prop.getProperty("maxTotal");

        if (defSeconds == 0) {
            if (Utils.isNotEmpty(defSeconds_str)) {
                defSeconds = Integer.parseInt(defSeconds_str);
            }
        }

        int db = 0;
        int maxTotal = 200;

        if (Utils.isNotEmpty(db_str)) {
            db = Integer.parseInt(db_str);
        }

        if (Utils.isNotEmpty(maxTotal_str)) {
            maxTotal = Integer.parseInt(maxTotal_str);
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

        _redisClient = new RedisClient(prop, db, maxTotal);
        _serializer = JavabinSerializer.instance;
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if(obj == null){
            return;
        }

        if (_redisClient != null) {
            String newKey = newKey(key);
            try {
                String val = _serializer.serialize(obj);

                if(seconds > 0) {
                    _redisClient.open0((ru) -> ru.key(newKey).expire(seconds).set(val));
                }else{
                    _redisClient.open0((ru) -> ru.key(newKey).expire(_defaultSeconds).set(val));
                }
            } catch (Exception ex) {
                EventBus.push(ex);
            }
        }
    }

    @Override
    public Object get(String key) {
        if (_redisClient != null) {
            String newKey = newKey(key);
            String val = _redisClient.open1((ru) -> ru.key(newKey).get());

            if(val == null){
                return null;
            }

            try {
                return _serializer.deserialize(val);
            } catch (Exception ex) {
                EventBus.push(ex);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void remove(String key) {
        if (_redisClient != null) {
            String newKey = newKey(key);
            _redisClient.open0((ru) -> {
                ru.key(newKey).delete();
            });
        }
    }


    protected String newKey(String key) {
        return _cacheKeyHead + "$" + Utils.md5(key);
    }
}
