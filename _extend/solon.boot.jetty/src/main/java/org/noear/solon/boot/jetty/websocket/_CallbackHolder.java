package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.WriteCallback;
import org.noear.solon.core.message.Callback;

/**
 * @author noear
 * @since 1.6
 */
class _CallbackHolder implements WriteCallback {
    Callback real;
    public _CallbackHolder(Callback callback){
        real = callback;
    }

    @Override
    public void writeFailed(Throwable e) {
        if(real != null) {
            real.onFailed(e);
        }
    }

    @Override
    public void writeSuccess() {
        if(real != null) {
            real.onSuccess();
        }
    }
}
