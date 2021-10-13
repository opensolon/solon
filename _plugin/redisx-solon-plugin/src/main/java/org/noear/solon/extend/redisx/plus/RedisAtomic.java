package org.noear.solon.extend.redisx.plus;

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
        return client.openAndGet(session -> session.key(atomicName).getAsLong());
    }

    public long increment() {
        return client.openAndGet(session -> session.key(atomicName).expire(-2).incr());
    }

    public long incrementBy(long num) {
        return client.openAndGet(session -> session.key(atomicName).expire(-2).incr(num));
    }

    public long decrement() {
        return client.openAndGet(session -> session.key(atomicName).expire(-2).decr());
    }

    public long decrementBy(long num) {
        return client.openAndGet(session -> session.key(atomicName).expire(-2).incr(-num));
    }
}
