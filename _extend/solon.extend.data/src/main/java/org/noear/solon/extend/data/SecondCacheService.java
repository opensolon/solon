package org.noear.solon.extend.data;

import org.noear.solon.core.cache.CacheService;

/**
 * 二级缓存
 * */
public class SecondCacheService implements CacheService {
    private CacheService cache1;
    private CacheService cache2;
    private int bufferSeconds;


    public SecondCacheService(CacheService cache1, CacheService cache2) {
        this(cache1, cache2, 5);
    }

    public SecondCacheService(CacheService cache1, CacheService cache2, int bufferSeconds) {
        this.cache1 = cache1;
        this.cache2 = cache2;
        this.bufferSeconds = bufferSeconds;
    }

    @Override
    public void store(String key, Object obj, int seconds) {
        cache1.store(key, obj, seconds);
        cache2.store(key, obj, seconds);
    }

    @Override
    public Object get(String key) {
        Object temp = cache1.get(key);
        if (temp == null) {
            temp = cache2.get(key);
            if (bufferSeconds > 0 && temp != null) {
                cache1.store(key, temp, bufferSeconds);
            }
        }
        return temp;
    }

    @Override
    public void remove(String key) {
        cache2.remove(key);
        cache1.remove(key);
    }
}
