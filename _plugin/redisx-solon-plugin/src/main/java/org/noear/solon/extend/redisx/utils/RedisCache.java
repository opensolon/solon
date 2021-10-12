package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisX;

/**
 * @author noear
 * @since 1.5
 */
public class RedisCache {
    private final RedisX redisX;

    public RedisCache(RedisX redisX) {
        this.redisX = redisX;
    }


    public void store(String key, String val, int seconds) {
        redisX.open0(session -> session.key(key).expire(seconds).valSet(val));
    }

    public String get(String key) {
        return redisX.open1(session -> session.key(key).val());
    }

    public void remove(String key) {
        redisX.open0(session -> session.key(key).delete());
    }
}
