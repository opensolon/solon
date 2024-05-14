package org.noear.solon.net.stomp.impl;

import org.noear.solon.net.websocket.WebSocket;

import java.util.HashSet;
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
public final class StompMessageOperations {
    /**
     * session存放
     */
    private final Map<String, WebSocket> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 地址与session映射
     */
    private final Set<DestinationInfo> destinationInfoSet = new HashSet<>();

    /**
     * 地址匹配正则
     */
    private final ConcurrentHashMap<String, Pattern> destinationMatch = new ConcurrentHashMap<>();

    /**
     *
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
