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

import org.noear.solon.net.websocket.WebSocket;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * stomp 操作缓存类
 *
 * @author limliu
 * @since 2.8
 */
public final class StompBrokerOperations {
    /**
     * session存放
     */
    private final Map<String, WebSocket> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 地址与session映射
     */
    private final Set<DestinationInfo> destinationInfoSet = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * 地址匹配正则
     */
    private final ConcurrentHashMap<String, Pattern> destinationMatch = new ConcurrentHashMap<>();

    /**
     * 消息编码器
     */
    private final MessageCodec msgCodec = new MessageCodecImpl();

    public Map<String, WebSocket> getWebSocketMap() {
        return webSocketMap;
    }

    public Set<DestinationInfo> getDestinationInfoSet() {
        return destinationInfoSet;
    }

    public ConcurrentHashMap<String, Pattern> getDestinationMatch() {
        return destinationMatch;
    }

    public MessageCodec getMsgCodec() {
        return msgCodec;
    }
}
