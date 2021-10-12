package org.noear.solon.extend.redisx.utils;


import org.noear.solon.extend.redisx.RedisX;

import java.util.Properties;

/**
 * Redis 锁
 *
 * @author noear
 * @since 1.5
 * */
public class RedisLock {
    private final RedisX redisX;

    public RedisLock(Properties prop) {
        redisX = new RedisX(prop);
    }

    public RedisLock(RedisX redisX) {
        this.redisX = redisX;
    }

    /**
     * 尝试把 group_key 锁定给 inMaster
     *
     * @param group     分组
     * @param key       关键字
     * @param inSeconds 锁定时间
     * @param inMaster  锁持有人
     */
    public boolean tryLock(String group, String key, int inSeconds, String inMaster) {
        String key2 = group + ".lk." + key;

        return redisX.open1((ru) -> ru.key(key2).expire(inSeconds).lock(inMaster));
    }

    /**
     * 尝试把 group_key 锁定
     *
     * @param inSeconds 锁定时间
     */
    public boolean tryLock(String group, String key, int inSeconds) {
        String key2 = group + ".lk." + key;

        return redisX.open1((ru) -> ru.key(key2).expire(inSeconds).lock("_"));
    }

    /**
     * 尝试把 group_key 锁定 （3秒）
     */
    public boolean tryLock(String group, String key) {
        return tryLock(group, key, 3);
    }

    /**
     * 检查 group_key 是否已被锁定
     */
    public boolean isLocked(String group, String key) {
        String key2 = group + ".lk." + key;

        return redisX.open1((ru) -> ru.key(key2).exists());
    }

    public String getLockedValue(String group, String key) {
        String key2 = group + ".lk." + key;
        return redisX.open1((ru) -> ru.key(key2).val());
    }


    /**
     * 解锁 group_key
     */
    public void unLock(String group, String key, String inMaster) {
        String key2 = group + ".lk." + key;

        redisX.open0((ru) -> {
            if (inMaster == null || inMaster.equals(ru.key(key2).val())) {
                ru.key(key2).delete();
            }
        });
    }

    /**
     * 解锁 group_key
     */
    public void unLock(String group, String key) {
        unLock(group, key, null);
    }
}
