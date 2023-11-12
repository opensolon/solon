package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketCallback;
import io.undertow.websockets.core.WebSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.6
 */
class CallbackImpl implements WebSocketCallback<Void> {
    static final Logger log = LoggerFactory.getLogger(CallbackImpl.class);

    public static final WebSocketCallback<Void> instance = new CallbackImpl();

    @Override
    public void complete(WebSocketChannel webSocketChannel, Void unused) {

    }

    @Override
    public void onError(WebSocketChannel webSocketChannel, Void unused, Throwable e) {
        log.warn(e.getMessage() ,e);
    }
}
