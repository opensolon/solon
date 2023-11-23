package org.noear.solon.net.websocket;

/**
 * @author noear
 * @since 2.6
 */
public abstract class WebSocketTimeoutBase extends WebSocketBase{
    private long idleTimeout;
    @Override
    public long getIdleTimeout() {
        return idleTimeout;
    }

    @Override
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }
}
