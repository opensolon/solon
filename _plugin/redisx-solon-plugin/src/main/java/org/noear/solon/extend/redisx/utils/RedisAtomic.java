package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;

/**
 * Redis 原子数字
 *
 * @author noear
 * @since 1.5
 */
public class RedisAtomic {
    private final RedisClient client;
    private final String atomicName;

    public RedisAtomic(RedisClient client, String atomicName) {
        this.client = client;
        this.atomicName = atomicName;
    }

    public long get(){
        return client.open1(session -> session.key(atomicName).getAsLong());
    }

    public long increment() {
        return client.open1(session -> session.key(atomicName).expire(-2).incr());
    }

    public long incrementBy(long num) {
        return client.open1(session -> session.key(atomicName).expire(-2).incr(num));
    }

    public long decrement() {
        return client.open1(session -> session.key(atomicName).expire(-2).decr());
    }

    public long decrementBy(long num) {
        return client.open1(session -> session.key(atomicName).expire(-2).incr(-num));
    }
}
