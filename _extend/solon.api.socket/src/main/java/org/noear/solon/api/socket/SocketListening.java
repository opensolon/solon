package org.noear.solon.api.socket;

public interface SocketListening {
    void onOpen(Session session);

    void onMessage(Session session, SocketMessage message);

    void onClose(Session session);

    void onError(Session session, Throwable throwable);
}
