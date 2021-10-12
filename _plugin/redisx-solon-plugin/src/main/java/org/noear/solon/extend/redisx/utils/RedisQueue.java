package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisX;

import java.util.Properties;

/**
 * @author noear 2021/10/12 created
 */
public class RedisQueue {
    private final RedisX redisX;

    public RedisQueue(Properties prop) {
        redisX = new RedisX(prop);
    }

    public RedisQueue(Properties prop, int db) {
        redisX = new RedisX(prop, db);
    }

    public RedisQueue(RedisX redisX) {
        this.redisX = redisX;
    }
}
