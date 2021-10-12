package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisX;

import java.util.Properties;

/**
 * @author noear
 * @since 1.5
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

    public void add(String queueName, String item) {
        redisX.open0(session -> session.key(queueName).expire(-2).listAdd(item));
    }

    public String poll(String queueName) {
        return redisX.open1(session -> session.key(queueName).listPop());
    }

    public String peek(String queueName) {
        return redisX.open1(session -> session.key(queueName).listPeek());
    }
}
