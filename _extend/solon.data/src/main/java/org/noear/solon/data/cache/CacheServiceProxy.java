package org.noear.solon.data.cache;

import org.noear.solon.Utils;

import java.util.Properties;

/**
 * @author noear
 * @since 1.5
 */
public class CacheServiceProxy implements CacheService {
    private CacheService real;
    private String driverType;

    /**
     * @param prop 需要有 driverType[local,redis,memcached] 属性申明
     * */
    public CacheServiceProxy(Properties prop) {
        String driverType = prop.getProperty("driverType");

        if ("local".equals(driverType)) {
            //本地缓存
            real = new LocalCacheService(prop);
        } else if ("redis".equals(driverType)) {
            //redis缓存
            real = newInstance("org.noear.solon.cache.jedis.RedisCacheService", prop);
        } else if ("memcached".equals(driverType)) {
            //memcached缓存
            real = newInstance("org.noear.solon.cache.spymemcached.MemCacheService", prop);
        } else {
            throw new IllegalArgumentException("There is no supported driver type");
        }
    }

    private CacheService newInstance(String clzName, Properties prop) {
        try {
            Class<?> clz = Utils.loadClass(clzName);
            if (clz == null) {
                throw new ClassNotFoundException(clzName);
            }

            return Utils.newInstance(clz, prop);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        real.store(key, obj, seconds);
    }

    @Override
    public Object get(String key) {
        return real.get(key);
    }

    @Override
    public void remove(String key) {
        real.remove(key);
    }
}
