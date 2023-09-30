package org.noear.weed.solon.plugin;

import org.noear.solon.data.cache.CacheService;
import org.noear.weed.cache.ICacheServiceEx;

/**
 * @author noear
 * @since 1.3
 */
public class CacheWrap implements ICacheServiceEx, CacheService {
    public static CacheWrap wrap(ICacheServiceEx cache){
        return new CacheWrap(cache);
    }


    ICacheServiceEx real;

    public CacheWrap(ICacheServiceEx real) {
        this.real = real;
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
    public <T> T get(String key, Class<T> clz) {
        return (T)real.get(key);
    }

    @Override
    public void remove(String key) {
        real.remove(key);
    }


    @Override
    public int getDefalutSeconds() {
        return real.getDefalutSeconds();
    }

    @Override
    public String getCacheKeyHead() {
        return real.getCacheKeyHead();
    }
}
