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
package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.core.util.PathMatcher;

/**
 * 订阅信息
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public class SubscriptionInfo {
    private final String sessionId;
    private final String subscriptionId;
    private final String destination;
    private final PathMatcher destinationAnalyzer;

    public SubscriptionInfo(String sessionId, String destination, String subscriptionId) {
        this.sessionId = sessionId;
        this.destination = destination;
        this.subscriptionId = subscriptionId;
        this.destinationAnalyzer = PathMatcher.get(destination);
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
     * 订阅者ID
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * 是否匹配
     */
    public boolean matches(String destination) {
        return destinationAnalyzer.matches(destination);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof SubscriptionInfo)) {
            return false;
        }

        SubscriptionInfo target = (SubscriptionInfo) obj;
        if (sessionId.equals(target.sessionId) && destination.equals(target.destination)) {
            return true;
        }

        return false;
    }
}
