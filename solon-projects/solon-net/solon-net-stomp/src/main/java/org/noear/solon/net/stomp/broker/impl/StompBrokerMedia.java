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

import org.noear.solon.core.util.RankEntity;
import org.noear.solon.net.stomp.StompSession;
import org.noear.solon.net.stomp.broker.FrameCodec;
import org.noear.solon.net.stomp.listener.StompListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stomp 代理端媒介
 *
 * @author noear
 * @since 3.0
 */
public class StompBrokerMedia {
    public static final FrameCodec codec = new FrameCodecImpl();

    /**
     * 监听器集合
     */
    public final List<RankEntity<StompListener>> listeners = new ArrayList<>();

    /**
     * 会话Id集合
     */
    public final Map<String, StompSession> sessionIdMap = new ConcurrentHashMap<>();

    /**
     * 会话Name集合
     */
    public final Map<String, List<StompSession>> sessionNameMap = new ConcurrentHashMap<>();

    /**
     * 订阅集合
     */
    public final Set<Subscription> subscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 发射器
     */
    public final StompBrokerEmitter emitter = new StompBrokerEmitter(this);


    /**
     * 经理地址前缀
     */
    public final Set<String> brokerDestinationPrefixes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 是否为经理地址
     */
    public boolean isBrokerDestination(String destination) {
        if (brokerDestinationPrefixes.size() == 0) {
            //没有启用
            return false;
        }

        for (String p1 : brokerDestinationPrefixes) {
            if (destination.startsWith(p1)) {
                return true;
            }
        }

        return false;
    }
}