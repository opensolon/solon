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

import org.noear.solon.net.stomp.FrameCodec;
import org.noear.solon.net.stomp.FrameCodecDefault;
import org.noear.solon.net.websocket.WebSocket;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Stomp 服务端操作缓存
 *
 * @author limliu
 * @since 2.8
 */
public class StompServerOperations {
    protected StompServerOperations() {

    }

    /**
     * session 存放
     */
    private final Map<String, WebSocket> sessionMap = new ConcurrentHashMap<>();

    /**
     * 地址与 session 映射
     */
    private final Set<SubscriptionInfo> subscriptionInfos = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 地址匹配正则
     */
    private final ConcurrentHashMap<String, Pattern> destinationMatchs = new ConcurrentHashMap<>();

    /**
     * 消息编码器
     */
    private final FrameCodec codec = new FrameCodecDefault();

    /**
     * 会话集合
     */
    public Map<String, WebSocket> getSessionMap() {
        return sessionMap;
    }

    /**
     * 订阅信息集合
     */
    public Set<SubscriptionInfo> getSubscriptionInfos() {
        return subscriptionInfos;
    }

    /**
     * 目的地匹配集合
     */
    public ConcurrentHashMap<String, Pattern> getDestinationMatchs() {
        return destinationMatchs;
    }

    /**
     * 编解码器
     */
    public FrameCodec getCodec() {
        return codec;
    }
}