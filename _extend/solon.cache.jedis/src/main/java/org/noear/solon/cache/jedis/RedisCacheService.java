package org.noear.solon.cache.jedis;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.core.cache.CacheService;

import java.util.Properties;

/**
 * @author noear
 * @since 1.3
 */
public class RedisCacheService implements CacheService {
    private String _cacheKeyHead;
    private int _defaultSeconds;

    private RedisX _cache = null;

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
            if(Utils.isEmpty(defSeconds_str) == false){
                defSeconds = Integer.parseInt(defSeconds_str);
            }
        }

        int db = 1;
        int maxTotaol = 200;

        if (Utils.isEmpty(db_str) == false) {
            db = Integer.parseInt(db_str);
        }

        if (Utils.isEmpty(maxTotaol_str) == false) {
            maxTotaol = Integer.parseInt(maxTotaol_str);
        }


        init0(keyHeader, defSeconds, server, password, db, maxTotaol);
    }

    protected void init0(String keyHeader, int defSeconds, String server, String password, int db, int maxTotaol) {
        if (db < 1) {
            db = 1;
        }

        if (maxTotaol < 10) {
            maxTotaol = 10;
        }

        _cacheKeyHead = keyHeader;
        _defaultSeconds = defSeconds;

        if (_defaultSeconds < 3) {
            _defaultSeconds = 30;
        }

        _cache = new RedisX(server, password, db, maxTotaol);
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        if (_cache != null) {
            String newKey = newKey(key);
            try {
                String val = ONode.serialize(obj);

                if(seconds > 0) {
                    _cache.open0((ru) -> ru.key(newKey).expire(seconds).set(val));
                }else{
                    _cache.open0((ru) -> ru.key(newKey).expire(_defaultSeconds).set(val));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public Object get(String key) {
        if (_cache != null) {
            String newKey = newKey(key);
            String val = _cache.open1((ru) -> ru.key(newKey).get());
            try {
                return ONode.deserialize(val);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
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
