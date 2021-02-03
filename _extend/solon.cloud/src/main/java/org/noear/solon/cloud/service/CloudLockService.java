package org.noear.solon.cloud.service;

/**
 * 云端锁服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudLockService {
    /**
     * 锁住
     *
     * @param group 锁分组
     * @param key 锁键
     * @param seconds 锁定时间（过期失效）
     * @param holder 持有人
     * */
    boolean lock(String group, String key, int seconds, String holder);

    /**
     * 锁住
     *
     * @param group 锁分组
     * @param key 锁键
     * @param seconds 锁定时间（过期失效）
     * */
    default boolean lock(String group, String key, int seconds) {
        return lock(group, key, seconds, null);
    }

    /**
     * 解锁
     *
     * @param group 锁分组
     * @param key 锁键
     * */
    void unlock(String group, String key, String holder);

    /**
     * 解锁
     *
     * @param group 锁分组
     * @param key 锁键
     * */
    default void unlock(String group, String key) {
        unlock(group, key, null);
    }

    /**
     * 检测是否存在锁
     *
     * @param group 锁分组
     * @param key 锁键
     * */
    boolean isLocked(String group, String key);

    /**
     * 获取锁的持有人
     *
     * @param group 锁分组
     * @param key 锁键
     * */
    String getLockHolder(String group, String key);
}
