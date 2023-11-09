package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.net.websocket.Handshake;
import org.noear.solon.net.websocket.WebSocketBase;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.impl.WebSocketRequestImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class _WebSokcetImpl extends WebSocketBase {
    private final WebSocketRequest request;
    private final WebSocketResponse real;
    private boolean isOpen = true;
    public _WebSokcetImpl(WebSocketRequest request) {
        this.request = request;
        this.real = ((WebSocketRequestImpl) request).getResponse();
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public boolean isValid() {
        return isOpen;
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public Handshake getHandshake() {
        return null;
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

    private boolean isClosed;

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void close() throws IOException {
        real.close();
    }
}
