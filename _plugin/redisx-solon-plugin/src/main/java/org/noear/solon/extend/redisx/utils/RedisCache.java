package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;

/**
 * @author noear
 * @since 1.5
 */
public class RedisCache {
    private final RedisClient client;

    public RedisCache(RedisClient client) {
        this.client = client;
    }


    public void store(String key, String val, int seconds) {
        client.open0(session -> session.key(key).expire(seconds).valSet(val));
    }

    public String get(String key) {
        return client.open1(session -> session.key(key).val());
    }

    public void remove(String key) {
        client.open0(session -> session.key(key).delete());
    }
}
