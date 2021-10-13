package org.noear.solon.extend.redisx.plus;

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


    public void store(String key, String val, int inSeconds) {
        client.open0(session -> session.key(key).expire(inSeconds).set(val));
    }

    public String get(String key) {
        return client.open1(session -> session.key(key).get());
    }

    public void remove(String key) {
        client.open0(session -> session.key(key).delete());
    }
}
