package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
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
    private WebSocketChannel real;

    public _WebSocketImpl(WebSocketChannel real) {
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
    public InetSocketAddress getRemoteAddress() {
        return real.getSourceAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return real.getDestinationAddress();
    }


    @Override
    public void send(String text) {
        WebSockets.sendText(text, real, _CallbackImpl.instance);
    }

    @Override
    public void send(ByteBuffer binary) {
        WebSockets.sendBinary(binary, real, _CallbackImpl.instance);
    }

    @Override
    public void close() throws IOException {
        real.close();
    }
}
