package org.noear.solon.extend.redisx.utils;

import org.noear.solon.extend.redisx.RedisClient;

/**
 * Redis 分布式锁
 *
 * @author noear
 * @since 1.5
 * */
public class RedisLock {
    private final RedisClient client;
    private final String lockName;

    public RedisLock(RedisClient client, String lockName) {
        this.client = client;
        this.lockName = lockName;
    }

    /**
     * 尝试把 group_key 锁定给 inMaster
     *
     * @param inSeconds 锁定时间
     * @param inMaster  锁持有人
     */
    public boolean tryLock(int inSeconds, String inMaster) {
        return client.open1((ru) -> ru.key(lockName).expire(inSeconds).lock(inMaster));
    }

    /**
     * 尝试把 group_key 锁定
     *
     * @param inSeconds 锁定时间
     */
    public boolean tryLock(int inSeconds) {
        return tryLock(inSeconds, "_");
    }

    /**
     * 尝试把 group_key 锁定 （3秒）
     */
    public boolean tryLock() {
        return tryLock(3);
    }

    /**
     * 检查是否已被锁定
     */
    public boolean isLocked() {
        return client.open1((ru) -> ru.key(lockName).exists());
    }

    /**
     * 获取锁的值
     */
    public String getValue() {
        return client.open1((ru) -> ru.key(lockName).get());
    }


    /**
     * 解锁
     */
    public void unLock(String inMaster) {
        client.open0((ru) -> {
            if (inMaster == null || inMaster.equals(ru.key(lockName).get())) {
                ru.key(lockName).delete();
            }
        });
    }

    /**
     * 解锁
     */
    public void unLock() {
        unLock(null);
    }
}
