/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
     * 尝试锁
     *
     * @param group   锁分组
     * @param key     锁键
     * @param seconds 锁定时间（过期失效）
     * @param holder  持有人
     * @return 是否成功
     */
    boolean tryLock(String group, String key, int seconds, String holder);

    /**
     * 尝试锁
     *
     * @param key     锁键
     * @param seconds 锁定时间（过期失效）
     * @param holder  持有人
     * @return 是否成功
     */
    default boolean tryLock(String key, int seconds, String holder) {
        return tryLock(Solon.cfg().appName(), key, seconds, holder);
    }

    /**
     * 尝试锁
     *
     * @param group   锁分组
     * @param key     锁键
     * @param seconds 锁定时间（过期失效）
     * @return 是否成功
     */
    default boolean tryLock(String group, String key, int seconds) {
        return tryLock(group, key, seconds, null);
    }

    /**
     * 尝试锁
     *
     * @param key     锁键
     * @param seconds 锁定时间（过期失效）
     * @return 是否成功
     */
    default boolean tryLock(String key, int seconds) {
        return tryLock(Solon.cfg().appName(), key, seconds);
    }

    /**
     * 解锁
     *
     * @param group 锁分组
     * @param key   锁键
     */
    void unLock(String group, String key, String holder);

    /**
     * 解锁
     *
     * @param group 锁分组
     * @param key   锁键
     */
    default void unLock(String group, String key) {
        unLock(group, key, null);
    }

    /**
     * 解锁
     *
     * @param key   锁键
     */
    default void unLock(String key) {
        unLock(Solon.cfg().appName(), key, null);
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
     * 获取持有人
     *
     * @param group 锁分组
     * @param key   锁键
     */
    String getHolder(String group, String key);

    /**
     * 获取持有人
     *
     * @param key   锁键
     */
    default String getHolder(String key) {
        return getHolder(Solon.cfg().appName(), key);
    }
}
