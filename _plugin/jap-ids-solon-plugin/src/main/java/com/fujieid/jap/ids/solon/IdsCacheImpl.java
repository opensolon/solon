package com.fujieid.jap.ids.solon;

import com.fujieid.jap.core.cache.JapCache;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.cache.CacheService;

import java.io.Serializable;

/**
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0.0
 * @date 2021-04-17 20:06
 * @since 1.0.0
 */
public class IdsCacheImpl implements JapCache {

    @Inject
    CacheService cacheService;

    /**
     * Set cache
     *
     * @param key   Cache key
     * @param value Cache value after serialization
     */
    @Override
    public void set(String key, Serializable value) {
        this.cacheService.store(key, value, -1);
    }

    /**
     * Set the cache and specify the expiration time of the cache
     *
     * @param key     Cache key
     * @param value   Cache value after serialization
     * @param timeout The expiration time of the cache, in milliseconds
     */
    @Override
    public void set(String key, Serializable value, long timeout) {
        this.cacheService.store(key, value, (int) timeout);
    }

    /**
     * Get cache value
     *
     * @param key Cache key
     * @return Cache value
     */
    @Override
    public Serializable get(String key) {
        return (Serializable) this.cacheService.get(key);
    }

    /**
     * Determine whether a key exists in the cache
     *
     * @param key Cache key
     * @return boolean
     */
    @Override
    public boolean containsKey(String key) {
        return this.get(key) != null;
    }

    /**
     * Delete the key from the cache
     *
     * @param key Cache key
     */
    @Override
    public void removeKey(String key) {
        this.cacheService.remove(key);
    }

}
