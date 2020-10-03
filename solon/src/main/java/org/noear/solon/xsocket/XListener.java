package org.noear.solon.xsocket;

/**
 * XSocket 监听器
 *
 * @author noear
 * @since 1.0
 * */
@FunctionalInterface
public interface XListener {

    default void onOpen(XSession session){}

    void onMessage(XSession session, XMessage message);

    default void onClose(XSession session){}

    default void onError(XSession session, Throwable error){}
}
