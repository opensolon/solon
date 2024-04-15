package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.WebSocketBase;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketImpl extends WebSocketBase {
    private WebSocketChannel real;

    public WebSocketImpl(WebSocketChannel real) {
        this.real = real;
        this.init(URI.create(real.getUrl()));
    }

    @Override
    public boolean isValid() {
        return isClosed() == false && real.isOpen();
    }

    @Override
    public boolean isSecure() {
        return real.isSecure();
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return real.getSourceAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return real.getDestinationAddress();
    }

    @Override
    public long getIdleTimeout() {
        return real.getIdleTimeout();
    }

    @Override
    public void setIdleTimeout(long idleTimeout) {
        real.setIdleTimeout(idleTimeout);
    }


    @Override
    public void send(String text) {
        WebSockets.sendText(text, real, CallbackImpl.instance);
    }

    @Override
    public void send(ByteBuffer binary) {
        WebSockets.sendBinary(binary, real, CallbackImpl.instance);
    }

    @Override
    public void close() {
        super.close();
        RunUtil.runAndTry(real::close);
    }
}
