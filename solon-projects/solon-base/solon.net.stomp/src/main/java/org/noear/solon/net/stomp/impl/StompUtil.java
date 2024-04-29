package org.noear.solon.net.stomp.impl;

import org.noear.solon.net.stomp.Header;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.websocket.WebSocket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * stomp 处理工具类
 *
 * @author limliu
 * @since 2.7
 */
public abstract class StompUtil {

    /**
     * session存放
     */
    public final static Map<String, WebSocket> WEB_SOCKET_MAP = new ConcurrentHashMap<>();

    /**
     * 地址与session映射
     */
    public final static Set<DestinationInfo> DESTINATION_INFO_SET = new HashSet<>();

    /**
     * 地址匹配正则
     */
    public final static ConcurrentHashMap<String, Pattern> DESTINATION_MATCH = new ConcurrentHashMap<>();

    public final static MessageCodec msgCodec = new MessageCodecImpl();

    /**
     * 指定链接发送消息
     *
     * @param webSocket
     * @param message
     */
    public static void send(WebSocket webSocket, Message message) {
        try {
            if (webSocket.isValid()) {
                webSocket.send(ByteBuffer.wrap(msgCodec.encode(message).getBytes(StandardCharsets.UTF_8)));
            }
        } finally {
            if (!webSocket.isValid()) {
                webSocket.close();
            }
        }
    }

    /**
     * 给指定通道发送消息
     *
     * @param destination
     * @param payload
     */
    public static void send(String destination, String payload) {
        send(destination, payload, null, null);
    }

    /**
     * 给指定通道发送消息
     *
     * @param destination
     * @param payload
     * @param headers
     */
    public static void send(String destination, String payload, List<Header> headers) {
        send(destination, payload, null, headers);
    }

    /**
     * 给指定通道发送消息
     *
     * @param destination
     * @param payload
     * @param contentType
     */
    public static void send(String destination, String payload, String contentType) {
        send(destination, payload, contentType, null);
    }

    /**
     * 给指定通道发送消息
     *
     * @param destination 通道，支持模糊匹配，如/topic/**
     * @param payload     消息内容
     * @param contentType 消息类型
     * @param headers     消息head信息
     */
    public static void send(String destination, String payload, String contentType, List<Header> headers) {
        StompUtil.DESTINATION_INFO_SET.parallelStream().filter(destinationInfo -> {
            return StompUtil.DESTINATION_MATCH.get(destinationInfo.getDestination()).matcher(destination).matches();
        }).forEach(destinationInfo -> {
            WebSocket sendSocket = StompUtil.WEB_SOCKET_MAP.get(destinationInfo.getSessionId());
            if (sendSocket == null) {
                return;
            }
            send(sendSocket, transform(Commands.MESSAGE, destinationInfo.getDestination(), payload, contentType, Arrays.asList(new Header(Header.SUBSCRIPTION, destinationInfo.getSubscription()), new Header(Header.MESSAGE_ID, UUID.randomUUID().toString()))));
        });
    }

    /**
     * 消息转换
     *
     * @param command
     * @param destination
     * @param payload
     * @return
     */
    public static Message transform(String command, String destination, String payload) {
        return transform(command, destination, payload, null, null);
    }

    /**
     * 消息转换
     *
     * @param command
     * @param destination
     * @param payload
     * @param contentType
     * @return
     */
    public static Message transform(String command, String destination, String payload, String contentType) {
        return transform(command, destination, payload, contentType, null);
    }

    /**
     * 消息转换
     *
     * @param command
     * @param destination
     * @param payload
     * @param headers
     * @return
     */
    public static Message transform(String command, String destination, String payload, List<Header> headers) {
        return transform(command, destination, payload, null, headers);
    }

    /**
     * 消息转换
     *
     * @param command
     * @param destination
     * @param payload
     * @param contentType
     * @param headers
     * @return
     */
    public static Message transform(String command, String destination, String payload, String contentType, List<Header> headers) {
        Message replyMessage = new MessageImpl(command, payload);
        if (contentType != null && contentType.length() > 0) {
            replyMessage.addHeader(Header.CONTENT_TYPE, contentType);
        }
        replyMessage.addHeader(Header.DESTINATION, destination);
        if (headers != null && headers.size() > 0) {
            replyMessage.getHeaderAll().addAll(headers);
        }
        return replyMessage;
    }


}
