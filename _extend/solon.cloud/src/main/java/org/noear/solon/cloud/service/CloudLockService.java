package org.noear.solon.cloud.service;

import org.noear.solon.Solon;

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
     * @param group   锁分组
     * @param key     锁键
     * @param seconds 锁定时间（过期失效）
     * @param holder  持有人
     * @return 是否成功
     */
    boolean lock(String group, String key, int seconds, String holder);

    /**
     * 锁住
     *
     * @param key     锁键
     * @param seconds 锁定时间（过期失效）
     * @param holder  持有人
     * @return 是否成功
     */
    default boolean lock(String key, int seconds, String holder) {
        return lock(Solon.cfg().appName(), key, seconds, holder);
    }

    /**
     * 锁住
     *
     * @param group   锁分组
     * @param key     锁键
     * @param seconds 锁定时间（过期失效）
     * @return 是否成功
     */
    default boolean lock(String group, String key, int seconds) {
        return lock(group, key, seconds, null);
    }

    /**
     * 锁住
     *
     * @param key     锁键
     * @param seconds 锁定时间（过期失效）
     * @return 是否成功
     */
    default boolean lock(String key, int seconds) {
        return lock(Solon.cfg().appName(), key, seconds);
    }

    /**
     * 解锁
     *
     * @param group 锁分组
     * @param key   锁键
     */
    void unlock(String group, String key, String holder);

    /**
     * 解锁
     *
     * @param group 锁分组
     * @param key   锁键
     */
    default void unlock(String group, String key) {
        unlock(group, key, null);
    }

    /**
     * 解锁
     *
     * @param key   锁键
     */
    default void unlock(String key) {
        unlock(Solon.cfg().appName(), key, null);
    }

    /**
     * 检测是否存在锁
     *
     * @param group 锁分组
     * @param key   锁键
     */
    boolean isLocked(String group, String key);

    /**
     * 检测是否存在锁
     *
     * @param key   锁键
     */
    default boolean isLocked(String key) {
        return isLocked(Solon.cfg().appName(), key);
    }

    /**
     * 获取锁的持有人
     *
     * @param group 锁分组
     * @param key   锁键
     */
    String getLockHolder(String group, String key);

    /**
     * 获取锁的持有人
     *
     * @param key   锁键
     */
    default String getLockHolder(String key) {
        return getLockHolder(Solon.cfg().appName(), key);
    }
}
