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

    default void sendTo(WebSocket session, String payload) {
        sendTo(session, Message.newBuilder().payload(payload).build());
    }

    void sendTo(String destination, Message message);

    default void sendTo(String destination, String payload) {
        sendTo(destination, Message.newBuilder().payload(payload).build());
    }
}
