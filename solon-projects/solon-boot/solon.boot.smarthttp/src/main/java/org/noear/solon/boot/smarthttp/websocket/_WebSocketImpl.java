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
public class _WebSocketImpl extends WebSocketBase {
    private final WebSocketRequest request;
    private final WebSocketResponse real;
    public _WebSocketImpl(WebSocketRequest request) {
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
    public InetSocketAddress getRemoteAddress() {
        return request.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return request.getLocalAddress();
    }

    @Override
    public void send(String text) {
        real.sendTextMessage(text);
    }

    @Override
    public void send(ByteBuffer binary) {
        real.sendBinaryMessage(binary.array());
    }

    @Override
    public void close() {
        super.close();
        real.close();
    }
}
