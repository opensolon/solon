package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.Utils;
import org.noear.solon.net.websocket.WebSocketBase;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.impl.WebSocketRequestImpl;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class WebSocketImpl extends WebSocketBase {
    private final WebSocketRequest request;
    private final WebSocketResponse real;
    public WebSocketImpl(WebSocketRequest request) {
        this.request = request;
        this.real = ((WebSocketRequestImpl) request).getResponse();
        this.init(buildUri(request));
    }

    public URI buildUri(WebSocketRequest req) {
        if (Utils.isEmpty(req.getQueryString())) {
            return URI.create(req.getRequestURL());
        } else {
            if (req.getRequestURL().contains("?")) {
                return URI.create(req.getRequestURL());
            } else {
                return URI.create(req.getRequestURL() + "?" + req.getQueryString());
            }
        }
    }

    @Override
    public boolean isValid() {
        return isClosed() == false;
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return request.getRemoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return request.getLocalAddress();
    }

    private long idleTimeout;
    @Override
    public long getIdleTimeout() {
        return idleTimeout;
    }

    @Override
    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    @Override
    public void send(String text) {
        real.sendTextMessage(text);
        real.flush();
    }

    @Override
    public void send(ByteBuffer binary) {
        real.sendBinaryMessage(binary.array());
        real.flush();
    }

    @Override
    public void close() {
        super.close();
        real.close();
    }
}
