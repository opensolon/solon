package org.noear.solon.net.websocket;

/**
 * WebSoskcet 监听器提供者
 *
 * @author noear
 * @since 2.7
 */
public interface WebSocketListenerSupplier {
    WebSocketListener getWebSocketListener();
}
