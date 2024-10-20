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

import org.noear.solon.core.util.KeyValues;
import org.noear.solon.net.stomp.StompSession;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stomp 服务端操作缓存
 *
 * @author limliu
 * @since 2.8
 */
public class StompServerOperations {
    protected StompServerOperations() {

    }

    private final Map<String, StompSession> sessionIdMap = new ConcurrentHashMap<>();
    private final Map<String, KeyValues<StompSession>> sessionNameMap = new ConcurrentHashMap<>();
    private final Set<SubscriptionInfo> subscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>());


    /**
     * 会话Id集合
     */
    public Map<String, StompSession> getSessionIdMap() {
        return sessionIdMap;
    }

    /**
     * 会话Name集合
     */
    public Map<String, KeyValues<StompSession>> getSessionNameMap() {
        return sessionNameMap;
    }

    /**
     * 订阅集合
     */
    public Set<SubscriptionInfo> getSubscriptions() {
        return subscriptions;
    }
}