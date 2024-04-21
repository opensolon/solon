package org.noear.solon.net.stomp;


import org.noear.solon.net.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 消息处理监听
 *
 * @author limliu
 * @since 2.7
 */
public abstract class StompListener {
    static Logger log = LoggerFactory.getLogger(StompListener.class);

    /**
     * session存放
     */
    protected final static Map<String, WebSocket> WEB_SOCKET_MAP = new ConcurrentHashMap<>();

    /**
     * 地址与session映射
     */
    protected final static Set<DestinationInfo> DESTINATION_INFO_SET = new CopyOnWriteArraySet<>();

    /**
     * 地址匹配正则
     */
    protected final static ConcurrentHashMap<String, Pattern> DESTINATION_MATCH = new ConcurrentHashMap<>();

    /**
     * 可以放鉴权，参数可以通过Head或者地址栏
     * 鉴权失败可以直接关闭, 如：socket.close();
     *
     * @param socket
     */
    public void onOpen(WebSocket socket) {
        WEB_SOCKET_MAP.put(socket.id(), socket);
    }

    /**
     * 连接关闭
     * 当连接断开时触发
     *
     * @param socket
     */
    public void onClose(WebSocket socket) {
        WEB_SOCKET_MAP.remove(socket);
        this.onUnsubscribe(socket, null);
    }

    /**
     * 连接命令
     * 需要响应
     *
     * @param socket
     * @param message
     */
    public void onConnect(WebSocket socket, Message message) {
        String heartBeat = message.getHeader(Header.HEART_BEAT);
        StompUtil.send(socket, new Message(Commands.CONNECTED, List.of(new Header(Header.HEART_BEAT, (heartBeat == null ? "0,0" : heartBeat)), new Header(Header.SERVER, "stomp"), new Header(Header.VERSION, "1.2"))));
    }

    /**
     * 断开命令
     * 需要响应
     *
     * @param socket
     * @param message
     */
    public void onDisconnect(WebSocket socket, Message message) {
        String receiptId = message.getHeader(Header.RECEIPT);
        StompUtil.send(socket, new Message(Commands.RECEIPT, List.of(new Header(Header.RECEIPT_ID, receiptId))));
    }

    /**
     * 订阅命令
     *
     * @param socket
     * @param message
     */
    public void onSubscribe(WebSocket socket, Message message) {
        final String subscriptionId = message.getHeader(Header.ID);
        final String destination = message.getHeader(Header.DESTINATION);
        if (destination == null || destination.length() == 0 || subscriptionId == null || subscriptionId.length() == 0) {
            StompUtil.send(socket, new Message(Commands.ERROR, "Required 'destination' or 'id' header missed"));
            return;
        }
        DestinationInfo destinationInfo = new DestinationInfo();
        destinationInfo.setSessionId(socket.id());
        destinationInfo.setDestination(destination);
        destinationInfo.setSubscription(subscriptionId);
        DESTINATION_INFO_SET.add(destinationInfo);
        if (!DESTINATION_MATCH.containsKey(destination)) {
            String destinationRegexp = "^" + destination.replaceAll("\\*\\*", ".+").replaceAll("\\*", ".+") + "$";
            DESTINATION_MATCH.put(destination, Pattern.compile(destinationRegexp));
        }
        final String receiptId = message.getHeader(Header.RECEIPT);
        if (receiptId != null) {
            StompUtil.send(socket, new Message(Commands.RECEIPT, List.of(new Header(Header.RECEIPT_ID, receiptId))));
        }
    }


    /**
     * 取消订阅命令
     *
     * @param socket
     * @param message
     */
    public void onUnsubscribe(WebSocket socket, Message message) {
        final String sessionId = socket.id();
        if (message == null) {
            this.unSubscribeHandle(destinationInfo -> {
                return sessionId.equals(destinationInfo.getSessionId());
            });
        } else {
            String subscription = message.getHeader(Header.ID);
            String destination = message.getHeader(Header.DESTINATION);
            this.unSubscribeHandle(destinationInfo -> {
                return sessionId.equals(destinationInfo.getSessionId()) && (destinationInfo.getDestination().equals(destination) || destinationInfo.getSubscription().equals(subscription));
            });
        }
    }

    /**
     * 发送消息
     *
     * @param socket
     * @param message
     */
    public void onSend(WebSocket socket, Message message) {
        String destination = message.getHeader(Header.DESTINATION);
        if (destination == null || destination.length() == 0) {
            StompUtil.send(socket, new Message(Commands.ERROR, "Required 'destination' header missed"));
            return;
        }
        DESTINATION_INFO_SET.parallelStream().filter(destinationInfo -> {
            return DESTINATION_MATCH.get(destinationInfo.getDestination()).matcher(destination).matches();
        }).forEach(destinationInfo -> {
            WebSocket sendSocket = WEB_SOCKET_MAP.get(destinationInfo.getSessionId());
            if (sendSocket == null) {
                return;
            }
            Message replyMessage = new Message(Commands.MESSAGE, message.getPayload());
            String contentType = message.getHeader(Header.CONTENT_TYPE);
            if (contentType != null && contentType.length() > 0) {
                replyMessage.headers(Header.CONTENT_TYPE, contentType);
            }
            replyMessage.headers(Header.DESTINATION, destinationInfo.getDestination()).headers(Header.MESSAGE_ID, UUID.randomUUID().toString()).headers(Header.SUBSCRIPTION, destinationInfo.getSubscription());
            StompUtil.send(sendSocket, replyMessage);
        });
    }

    /**
     * 消息ACK
     *
     * @param socket
     * @param message
     */
    public void onAck(WebSocket socket, Message message) {

    }


    /**
     * 删除订阅
     *
     * @param function
     */
    protected void unSubscribeHandle(Function<DestinationInfo, Boolean> function) {
        Iterator<DestinationInfo> iterator = DESTINATION_INFO_SET.iterator();
        while (iterator.hasNext()) {
            if (function.apply(iterator.next())) {
                iterator.remove();
            }
        }
    }
}
