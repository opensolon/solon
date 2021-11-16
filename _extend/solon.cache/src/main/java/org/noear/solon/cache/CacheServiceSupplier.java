package org.noear.solon.cache;

import org.noear.solon.data.cache.CacheService;
import org.noear.solon.data.cache.LocalCacheService;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * CacheService 供应者
 *
 * @author noear
 * @since 1.5
 */
public class CacheServiceSupplier implements Supplier<CacheService> {
    private CacheService real;
    private String driverType;

    public CacheServiceSupplier(Properties props) {
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
    public CacheService get() {
        return real;
    }
}
