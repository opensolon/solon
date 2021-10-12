package org.noear.solon.extend.redisx.utils;


import org.noear.solon.extend.redisx.RedisX;

import java.util.Properties;

/**
 * Redis Id
 *
 * @author noear
 * @since 1.5
 * */
public class RedisId {
    private final RedisX redisX;

    public RedisId(Properties prop) {
        redisX = new RedisX(prop);
    }

    public RedisId(RedisX redisX) {
        this.redisX = redisX;
    }

    public long newID(String group, String key) {
        return newID(group, key, 60 * 60 * 24 * 365 * 10);
    }

    public long newID(String group, String key, int inSeconds) {
        return redisX.open1((session) -> session.key(group).expire(inSeconds).hashIncr(key, 1l));
    }
}
