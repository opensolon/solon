package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;

/**
 * @author noear
 * @since 1.5
 */
public class RedisQueue {
    private final RedisClient redisX;
    private final String queueName;

    public RedisQueue(RedisClient redisX, String queueName) {
        this.redisX = redisX;
        this.queueName = queueName;
    }

    public void add(String item) {
        redisX.open0(session -> session.key(queueName).expire(-2).listAdd(item));
    }

    public String poll() {
        return redisX.open1(session -> session.key(queueName).listPop());
    }

    public String peek() {
        return redisX.open1(session -> session.key(queueName).listPeek());
    }
}
