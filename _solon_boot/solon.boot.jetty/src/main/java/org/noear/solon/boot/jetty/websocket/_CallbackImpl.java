package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.WriteCallback;
import org.noear.solon.core.event.EventBus;

/**
 * @author noear
 * @since 1.6
 */
class _CallbackImpl implements WriteCallback {
    public static final WriteCallback instance = new _CallbackImpl();

    @Override
    public void writeFailed(Throwable e) {
        EventBus.push(e);
    }

    @Override
    public void writeSuccess() {

    }
}
