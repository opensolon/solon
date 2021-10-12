package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;

/**
 * Redis 队列
 *
 * @author noear
 * @since 1.5
 */
public class RedisQueue {
    private final RedisClient client;
    private final String queueName;

    public RedisQueue(RedisClient client, String queueName) {
        this.client = client;
        this.queueName = queueName;
    }

    public void add(String item) {
        client.open0(session -> session.key(queueName).expire(-2).listAdd(item));
    }

    public String poll() {
        return client.open1(session -> session.key(queueName).listPop());
    }

    public String peek() {
        return client.open1(session -> session.key(queueName).listPeek());
    }
}
