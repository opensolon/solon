package org.noear.solon.boot.websocket.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.noear.solon.net.websocket.WebSocketTimeoutBase;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.6
 */
public class WebSocketImpl extends WebSocketTimeoutBase {
    private ChannelHandlerContext real;
    public WebSocketImpl(ChannelHandlerContext real) {
        this.real = real;
        this.init(URI.create(real.attr(WsServerHandler.ResourceDescriptorKey).get()));
    }

    @Override
    public boolean isValid() {
         return isClosed() == false && real.channel().isOpen();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress remoteAddress() {
        return (InetSocketAddress)real.channel().remoteAddress();
    }

    @Override
    public InetSocketAddress localAddress() {
        return (InetSocketAddress)real.channel().localAddress();
    }


    @Override
    public void send(String text) {
        real.writeAndFlush(new TextWebSocketFrame(text));
        onSend();
    }

    @Override
    public void send(ByteBuffer binary) {
        real.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(binary)));
        onSend();
    }

    @Override
    public void close() {
        super.close();
        real.close();
    }
}
