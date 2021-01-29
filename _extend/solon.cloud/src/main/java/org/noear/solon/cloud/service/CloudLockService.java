package org.noear.solon.cloud.service;

import org.noear.solon.Solon;

/**
 * 云端锁服务
 *
 * @author noear
 * @since 1.2
 */
public interface CloudLockService {
    default boolean lock(String key, int seconds) {
        return lock(Solon.cfg().appName(), key, seconds);
    }

    default void unlock(String key) {
        unlock(Solon.cfg().appName(), key);
    }

    default boolean isLocked(String key) {
        return isLocked(Solon.cfg().appName(), key);
    }


    boolean lock(String group, String key, int seconds);

    void unlock(String group, String key);

    boolean isLocked(String group, String key);

}
