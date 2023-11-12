package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import io.undertow.websockets.core.WebSockets;
import org.noear.solon.net.websocket.WebSocketBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketImpl extends WebSocketBase {
    private static final Logger log = LoggerFactory.getLogger(WebSocketImpl.class);
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
    public InetSocketAddress getRemoteAddress() {
        return real.getSourceAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return real.getDestinationAddress();
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
        try {
            real.close();
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug("{}", e);
            }
        }
    }
}
