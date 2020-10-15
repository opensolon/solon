package org.noear.solon.core;

/**
 * XSocket 监听器（实现 XMessage + XListener 架构）
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
