package org.noear.solon.net.stomp;

import org.noear.solon.net.websocket.WebSocket;

/**
 * 消息发送器
 *
 * @author noear
 * @since 3.0
 */
public interface StompMessageSender {
    void sendTo(WebSocket session, Message message);

    void sendTo(String destination, Message message);

    default void sendTo(String destination, String payload) {
        sendTo(destination, Message.newBuilder().payload(payload).build());
    }
}
