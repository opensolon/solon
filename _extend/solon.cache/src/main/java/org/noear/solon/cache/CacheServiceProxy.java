package org.noear.solon.cache;

import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.LocalCacheService;

import java.util.Properties;

/**
 * 缓存服务代理，可自动切换服务配置
 *
 * @author noear
 * @since 1.5
 */
public class CacheServiceProxy implements CacheService {
    private CacheService real;
    private String driverType;

    /**
     * @param props 需要有 driverType[local,redis,memcached] 属性申明
     * */
    public CacheServiceProxy(Properties props) {
        driverType = props.getProperty("driverType");

        if ("local".equals(driverType)) {
            //本地缓存
            real = new LocalCacheService(props);
        } else if ("redis".equals(driverType)) {
            //redis缓存
            real = new org.noear.solon.cache.jedis.RedisCacheService(props);
        } else if ("memcached".equals(driverType)) {
            //memcached缓存
            real = new org.noear.solon.cache.spymemcached.MemCacheService(props);
        } else {
            throw new IllegalArgumentException("There is no supported driverType");
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
