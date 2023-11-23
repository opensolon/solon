package org.noear.solon.net.websocket;

import org.noear.solon.core.util.RunUtil;

import java.util.concurrent.ScheduledFuture;

/**
 * @author noear
 * @since 2.6
 */
public abstract class WebSocketTimeoutBase extends WebSocketBase {

    private long idleTimeout;
    private ScheduledFuture<?> idleTimeoutFuture;
    private long liveTime;

    /**
     * 当发送时
     */
    public void onSend() {
        liveTime = System.currentTimeMillis();
    }

    /**
     * 当接收时
     */
    public void onReceive() {
        liveTime = System.currentTimeMillis();
    }

    @Override
    public long getIdleTimeout() {
        return idleTimeout;
    }

    @Override
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;

        initIdleTimeoutFuture();
    }

    private synchronized void initIdleTimeoutFuture() {
        liveTime = System.currentTimeMillis();

        if (idleTimeout > 0) {
            if (idleTimeoutFuture == null) {
                idleTimeoutFuture = RunUtil.delayAndRepeat(() -> {
                    if (liveTime + idleTimeout < System.currentTimeMillis()) {
                        if (log.isWarnEnabled()) {
                            log.warn("WebSocket idle timeout, will close!");
                        }

                        this.close();
                    }
                }, 10 * 1000);
            }
        } else {
            if (idleTimeoutFuture != null) {
                idleTimeoutFuture.cancel(true);
                idleTimeoutFuture = null;
            }
        }
    }
}
