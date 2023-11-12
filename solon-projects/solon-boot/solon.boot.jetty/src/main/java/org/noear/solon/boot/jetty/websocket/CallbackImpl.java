package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.WriteCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author noear
 * @since 1.6
 */
class CallbackImpl implements WriteCallback {
    static final Logger log = LoggerFactory.getLogger(CallbackImpl.class);

    public static final WriteCallback instance = new CallbackImpl();

    @Override
    public void writeFailed(Throwable e) {
        log.warn(e.getMessage(), e);
    }

    @Override
    public void writeSuccess() {

    }
}
