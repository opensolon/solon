package org.noear.solon.boot.websocket;

import org.noear.solon.net.websocket.Handshake;
import org.noear.solon.net.websocket.WebSocketBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.6
 */
public class _WebSocketImpl extends WebSocketBase {
    private final org.java_websocket.WebSocket real;

    public _WebSocketImpl(org.java_websocket.WebSocket real) {
        this.real = real;
    }

    @Override
    public boolean isValid() {
        return real.isOpen();
    }

    @Override
    public boolean isSecure() {
        return real.hasSSLSupport();
    }

    @Override
    public Handshake getHandshake() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return real.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return real.getLocalSocketAddress();
    }

    @Override
    public void send(String text) {
        real.send(text);
    }

    @Override
    public void send(ByteBuffer binary) {
        real.send(binary);
    }

    @Override
    public void close() throws IOException {
        real.close();
    }
}
