package org.noear.solon.net.stomp;


import org.noear.solon.net.websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 消息处理监听
 *
 * @author limliu
 * @since 2.7
 */
public final class StompListenerImpl implements IStompListener {

    static Logger log = LoggerFactory.getLogger(StompListenerImpl.class);


    /**
     * 可以放鉴权，参数可以通过Head或者地址栏
     * 鉴权失败可以直接关闭, 如：socket.close();
     *
     * @param socket
     */
    @Override
    public void onOpen(WebSocket socket) {
        StompUtil.WEB_SOCKET_MAP.put(socket.id(), socket);
    }

    /**
     * 连接关闭
     * 当连接断开时触发
     *
     * @param socket
     */
    @Override
    public void onClose(WebSocket socket) {
        StompUtil.WEB_SOCKET_MAP.remove(socket);
        this.onUnsubscribe(socket, null);
    }

    /**
     * 连接命令
     * 需要响应
     *
     * @param socket
     * @param message
     */
    @Override
    public void onConnect(WebSocket socket, Message message) {
        String heartBeat = message.getHeader(Header.HEART_BEAT);
        StompUtil.send(socket, new Message(Commands.CONNECTED, Arrays.asList(new Header(Header.HEART_BEAT, (heartBeat == null ? "0,0" : heartBeat)), new Header(Header.SERVER, "stomp"), new Header(Header.VERSION, "1.2"))));
    }

    /**
     * 断开命令
     * 需要响应
     *
     * @param socket
     * @param message
     */
    @Override
    public void onDisconnect(WebSocket socket, Message message) {
        String receiptId = message.getHeader(Header.RECEIPT);
        StompUtil.send(socket, new Message(Commands.RECEIPT, Arrays.asList(new Header(Header.RECEIPT_ID, receiptId))));
    }

    /**
     * 订阅命令
     *
     * @param socket
     * @param message
     */
    @Override
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
        StompUtil.DESTINATION_INFO_SET.add(destinationInfo);
        if (!StompUtil.DESTINATION_MATCH.containsKey(destination)) {
            String destinationRegexp = "^" + destination.replaceAll("\\*\\*", ".+").replaceAll("\\*", ".+") + "$";
            StompUtil.DESTINATION_MATCH.put(destination, Pattern.compile(destinationRegexp));
        }
        final String receiptId = message.getHeader(Header.RECEIPT);
        if (receiptId != null) {
            StompUtil.send(socket, new Message(Commands.RECEIPT, Arrays.asList(new Header(Header.RECEIPT_ID, receiptId))));
        }
    }


    /**
     * 取消订阅命令
     *
     * @param socket
     * @param message
     */
    @Override
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
    @Override
    public void onSend(WebSocket socket, Message message) {
        String destination = message.getHeader(Header.DESTINATION);
        if (destination == null || destination.length() == 0) {
            StompUtil.send(socket, new Message(Commands.ERROR, "Required 'destination' header missed"));
            return;
        }
        StompUtil.send(destination, message.getPayload(), message.getHeader(Header.CONTENT_TYPE));
    }

    /**
     * 消息ACK
     *
     * @param socket
     * @param message
     */
    @Override
    public void onAck(WebSocket socket, Message message) {

    }


    /**
     * 删除订阅
     *
     * @param function
     */
    protected void unSubscribeHandle(Function<DestinationInfo, Boolean> function) {
        Iterator<DestinationInfo> iterator = StompUtil.DESTINATION_INFO_SET.iterator();
        while (iterator.hasNext()) {
            if (function.apply(iterator.next())) {
                iterator.remove();
            }
        }
    }
}
