package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import org.noear.solon.core.message.Callback;

/**
 * @author noear
 * @since 1.6
 */
class _CallbackHolder implements WebSocketCallback<Void> {
    Callback real;

    public _CallbackHolder(Callback callback) {
        real = callback;
    }

    @Override
    public void complete(WebSocketChannel webSocketChannel, Void unused) {
        if (real != null) {
            real.onSuccess();
        }
    }

    @Override
    public void onError(WebSocketChannel webSocketChannel, Void unused, Throwable throwable) {
        if (real != null) {
            real.onFailed(throwable);
        }
    }
}
