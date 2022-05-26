package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import org.noear.solon.core.event.EventBus;

/**
 * @author noear
 * @since 1.6
 */
class _CallbackImpl implements WebSocketCallback<Void> {
    public static final WebSocketCallback<Void> instance = new _CallbackImpl();

    @Override
    public void complete(WebSocketChannel webSocketChannel, Void unused) {

    }

    @Override
    public void onError(WebSocketChannel webSocketChannel, Void unused, Throwable e) {
        EventBus.push(e);
    }
}
