package org.noear.solon.boot.websocket.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

public class _SocketServerSession extends SessionBase {


    public static final Map<ChannelHandlerContext, Session> sessions = new HashMap<>();

    public static Session get(ChannelHandlerContext real) {
        Session tmp = sessions.get(real);
        if (tmp == null) {
            synchronized (real) {
                tmp = sessions.get(real);
                if (tmp == null) {
                    tmp = new _SocketServerSession(real);
                    sessions.put(real, tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(ChannelHandlerContext real) {
        sessions.remove(real);
    }


    private final ChannelHandlerContext real;
    private final String _sessionId = Utils.guid();

    public _SocketServerSession(ChannelHandlerContext real) {
        this.real = real;
    }

    @Override
    public Object real() {
        return real;
    }


    @Override
    public String sessionId() {
        return _sessionId;
    }

    @Override
    public MethodType method() {
        return MethodType.WEBSOCKET;
    }

    private URI _uri;
    @Override
    public URI uri() {
        if(_uri == null){
            _uri = URI.create(real.attr(WsServerHandler.ResourceDescriptorKey).get());
        }

        return _uri;
    }

    private String _path;

    @Override
    public String path() {
        if (_path == null) {
            _path = uri().getPath();
        }

        return _path;
    }

    @Override
    public void sendAsync(Message message) {
        send(message);
    }

    @Override
    public void sendAsync(String message) {
        send(message);
    }

    @Override
    public void send(String message) {
        synchronized (this) {
            if (Solon.app().enableWebSocketD()) {
                ByteBuffer buf = ProtocolManager.encode(Message.wrap(message));
                real.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(buf)));
            } else {
                real.writeAndFlush(new TextWebSocketFrame(message));
            }
        }
    }

    @Override
    public void send(Message message) {
        super.send(message);

        synchronized (this) {
            if (Solon.app().enableWebSocketD()) {
                ByteBuffer buf = ProtocolManager.encode(message);
                real.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(buf)));
            } else {
                if (message.isString()) {
                    real.writeAndFlush(new TextWebSocketFrame(message.bodyAsString()));
                } else {
                    byte[] bytes = message.body();
                    real.writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer(bytes)));
                }
            }
        }
    }


    @Override
    public void close() throws IOException {
        if (real == null) {
            return;
        }

        real.close();
        sessions.remove(real);
    }

    @Override
    public boolean isValid() {
        if(real == null){
            return false;
        }

        return real.channel().isOpen();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress)real.channel().remoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress)real.channel().localAddress();
    }

    static AttributeKey AttachmentKey = AttributeKey.valueOf("Attachment");

    @Override
    public void setAttachment(Object obj) {
        real.attr(AttachmentKey).set(obj);
    }

    @Override
    public <T> T getAttachment() {
        return (T)real.attr(AttachmentKey).get();
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(sessions.values());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        _SocketServerSession that = (_SocketServerSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
