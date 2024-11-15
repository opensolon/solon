/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.core.handle;

import java.util.Collection;

/**
 * Session 状态器接口
 *
 * 用于对接http自带 sesssion 或 扩展 sesssion（可相互切换）
 *
 * @author noear
 * @since 1.0
 * */
public interface SessionState {
    /**
     * 是否可替换
     */
    default boolean replaceable() {
        return true;
    }

    /**
     * 创建时间
     */
    long creationTime();

    /**
     * 最后访问时间
     */
    long lastAccessTime();

    /**
     * 刷新SESSION状态（可续时）
     */
    default void sessionRefresh() {
    }

    /**
     * 发布SESSION状态(类似jwt)
     */
    default void sessionPublish() {
    }

    /**
     * 清除SESSION状态
     */
    void sessionClear();

    /**
     * 会话重置（清空数据，并变更会话ID）
     */
    void sessionReset();

    /**
     * 获取SESSION_ID
     */
    String sessionId();

    /**
     * 变更SESSION_ID
     */
    String sessionChangeId();

    /**
     * 获取SESSION键名集合
     */
    Collection<String> sessionKeys();

    /**
     * 获取SESSION状态
     */
    default Object sessionGet(String key) {
        return sessionGet(key, Object.class);
    }

    /**
     * 获取SESSION状态（指定类型）
     */
    <T> T sessionGet(String key, Class<T> clz);

    /**
     * 设置SESSION状态
     */
    void sessionSet(String key, Object val);

    /**
     * 移除SESSION状态
     */
    void sessionRemove(String key);

    /**
     * 获取会话令牌（如： solon.extend.sessionstate.jwt 插件支持）
     */
    default String sessionToken() {
        throw new UnsupportedOperationException();
    }
}
