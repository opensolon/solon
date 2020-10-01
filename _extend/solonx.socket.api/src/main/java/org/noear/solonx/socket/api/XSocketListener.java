package org.noear.solonx.socket.api;

@FunctionalInterface
public interface XSocketListener {
    default void onOpen(XSession session){}

    void onMessage(XSession session, XSocketMessage message);

    default void onClosing(XSession session){}

    default void onClose(XSession session){}

    default void onError(XSession session, Throwable throwable){}
}
