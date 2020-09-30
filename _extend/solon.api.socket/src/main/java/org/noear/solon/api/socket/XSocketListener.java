package org.noear.solon.api.socket;

public interface XSocketListener {
    void onOpen(XSession session);

    void onMessage(XSession session, XSocketMessage message);

    void onClosing(XSession session);

    void onClose(XSession session);

    void onError(XSession session, Throwable throwable);
}
