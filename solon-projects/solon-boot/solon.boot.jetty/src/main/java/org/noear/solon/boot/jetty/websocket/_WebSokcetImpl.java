package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.noear.solon.net.websocket.Handshake;
import org.noear.solon.net.websocket.WebSocketBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.6
 */
public class _WebSokcetImpl extends WebSocketBase {
    private final Session real;
    public _WebSokcetImpl(Session real){
        this.real = real;
    }
    @Override
    public boolean isValid() {
        return real.isOpen();
    }

    @Override
    public boolean isSecure() {
        return real.isSecure();
    }

    @Override
    public Handshake getHandshake() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() throws IOException {
        return real.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException {
        return real.getLocalAddress();
    }

    @Override
    public void send(String text) {
        real.getRemote().sendString(text, _CallbackImpl.instance);
    }

    @Override
    public void send(ByteBuffer binary) {
        real.getRemote().sendBytes(binary, _CallbackImpl.instance);
    }

    @Override
    public void close() throws IOException {
        real.close();
    }
}
