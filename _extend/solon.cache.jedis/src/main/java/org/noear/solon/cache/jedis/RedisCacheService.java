package org.noear.solon.cache.jedis;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.Serializer;

import java.util.Properties;

/**
 * @author noear
 * @since 1.3
 */
public class RedisCacheService implements CacheService {
    protected String _cacheKeyHead;
    protected int _defaultSeconds;
    protected final RedisX _cache;
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
        String server = prop.getProperty("server");
        String password = prop.getProperty("password");
        String db_str = prop.getProperty("db");
        String maxTotaol_str = prop.getProperty("maxTotaol");

        if (defSeconds == 0) {
            if (Utils.isEmpty(defSeconds_str) == false) {
                defSeconds = Integer.parseInt(defSeconds_str);
            }
        }

        int db = 0;
        int maxTotaol = 200;

        if (Utils.isNotEmpty(db_str)) {
            db = Integer.parseInt(db_str);
        }

        if (Utils.isNotEmpty(maxTotaol_str)) {
            maxTotaol = Integer.parseInt(maxTotaol_str);
        }

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        if (_defaultSeconds < 1) {
            _defaultSeconds = 30;
        }

        if (Utils.isEmpty(_cacheKeyHead)) {
            _cacheKeyHead = Solon.cfg().appName();
        }

        _cache = new RedisX(prop, server, password, db, maxTotaol);
        _serializer = JavabinSerializer.instance;
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if(obj == null){
            return;
        }

        if (_cache != null) {
            String newKey = newKey(key);
            try {
                String val = _serializer.serialize(obj);

                if(seconds > 0) {
                    _cache.open0((ru) -> ru.key(newKey).expire(seconds).set(val));
                }else{
                    _cache.open0((ru) -> ru.key(newKey).expire(_defaultSeconds).set(val));
                }
            } catch (Exception ex) {
                EventBus.push(ex);
            }
        }
    }

    @Override
    public Object get(String key) {
        if (_cache != null) {
            String newKey = newKey(key);
            String val = _cache.open1((ru) -> ru.key(newKey).get());

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
        if (_cache != null) {
            String newKey = newKey(key);
            _cache.open0((ru) -> {
                ru.key(newKey).delete();
            });
        }
    }


    private String newKey(String key) {
        return _cacheKeyHead + "$" + Utils.md5(key);
    }
}
