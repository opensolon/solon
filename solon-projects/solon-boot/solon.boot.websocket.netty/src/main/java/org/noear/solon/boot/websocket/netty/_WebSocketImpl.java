package org.noear.solon.boot.websocket.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
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
    private ChannelHandlerContext real;
    public _WebSocketImpl(ChannelHandlerContext real) {
        this.real = real;
    }

    @Override
    public boolean isValid() {
         return real.channel().isOpen();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public Handshake getHandshake() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress)real.channel().remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)real.channel().localAddress();
    }


    @Override
    public void send(String text) {
        real.writeAndFlush(new TextWebSocketFrame(text));
    }

    @Override
    public void send(ByteBuffer binary) {
        real.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(binary)));
    }

    @Override
    public void close() throws IOException {
        real.close();
    }
}
