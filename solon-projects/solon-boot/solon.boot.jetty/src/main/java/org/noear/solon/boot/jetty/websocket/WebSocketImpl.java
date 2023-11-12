package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.noear.solon.net.websocket.WebSocketBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketImpl extends WebSocketBase {
    private final Session real;
    public WebSocketImpl(Session real){
        this.real = real;
        this.init(real.getUpgradeRequest().getRequestURI());
    }

    @Override
    public boolean isValid() {
        return isClosed()== false && real.isOpen();
    }

    @Override
    public boolean isSecure() {
        return real.isSecure();
    }

    @Override
    public InetSocketAddress remoteAddress() throws IOException {
        return real.getRemoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() throws IOException {
        return real.getLocalAddress();
    }

    @Override
    public void send(String text) {
        real.getRemote().sendString(text, CallbackImpl.instance);
    }

    @Override
    public void send(ByteBuffer binary) {
        real.getRemote().sendBytes(binary, CallbackImpl.instance);
    }


    @Override
    public void close() {
        super.close();
        real.close();
    }
}
