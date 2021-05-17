package org.noear.weed.solon.plugin;

import org.noear.solon.core.cache.CacheService;
import org.noear.weed.cache.ICacheServiceEx;

/**
 * @author noear
 * @since 1.3
 */
public class CacheWarp implements ICacheServiceEx, CacheService {
    public static CacheWarp warp(ICacheServiceEx cache){
        return new CacheWarp(cache);
    }


    ICacheServiceEx real;

    public CacheWarp(ICacheServiceEx real) {
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
