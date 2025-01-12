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
package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.core.util.PathMatcher;

import java.util.Objects;

/**
 * 订阅信息
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public class Subscription {
    private final String sessionId;
    private final String destination;
    private final PathMatcher destinationMatcher;
    private final String id;

    /**
     * @param sessionId   会话Id
     * @param destination 目的地（支持模糊匹配，/topic/**）
     * @param id          订阅Id
     */
    public Subscription(String sessionId, String destination, String id) {
        this.sessionId = sessionId;
        this.destination = destination;
        this.id = id;
        this.destinationMatcher = PathMatcher.get(destination);
    }

    /**
     * 会话ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 目的地
     */
    public String getDestination() {
        return destination;
    }

    /**
     * 订阅ID
     */
    public String getId() {
        return id;
    }

    /**
     * 是否匹配
     */
    public boolean matches(String destination) {
        return destinationMatcher.matches(destination);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;

        Subscription that = (Subscription) o;
        return Objects.equals(sessionId, that.sessionId)
                && Objects.equals(destination, that.destination);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
