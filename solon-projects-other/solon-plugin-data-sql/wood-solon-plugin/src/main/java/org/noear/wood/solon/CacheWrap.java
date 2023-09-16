package org.noear.wood.solon;

import org.noear.solon.data.cache.CacheService;
import org.noear.wood.cache.ICacheServiceEx;

/**
 * 缓存包装器
 *
 * @author noear
 * @since 1.10
 */
public class CacheWrap implements ICacheServiceEx, CacheService {
    public static CacheWrap wrap(ICacheServiceEx cache) {
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

    /**
     * @deprecated 2.5
     * */
    @Deprecated
    @Override
    public Object get(String key) {
        return real.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> clz) {
        return real.get(key, clz);
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
