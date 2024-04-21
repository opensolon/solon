package org.noear.solon.net.stomp;


import org.noear.solon.net.websocket.WebSocket;

public interface StompListener {
    void onOpen(WebSocket socket);

    void onConnect(WebSocket socket, Message message);

    void onClose(WebSocket socket);

    void onDisconnect(WebSocket socket, Message message);

    void onSubscribe(WebSocket socket, Message message);

    void onUnsubscribe(WebSocket socket, Message message);

    void onSend(WebSocket socket, Message message);

    void onAck(WebSocket socket, Message message);
}
