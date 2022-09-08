package org.noear.solon.boot.socketd.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
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
    public static final Map<WebSocket, Session> sessions = new HashMap<>();

    public static Session get(WebSocket real) {
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

    public static void remove(WebSocket real) {
        sessions.remove(real);
    }


    private final WebSocket real;
    private final String _sessionId = Utils.guid();

    public _SocketServerSession(WebSocket real) {
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
        if (_uri == null) {
            _uri = URI.create(real.getResourceDescriptor());
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
    public void sendAsync(String message) {
        Utils.async(() -> {
            try {
                send(message);
            } catch (Throwable e) {
                EventBus.push(e);
            }
        });
    }

    @Override
    public void sendAsync(Message message) {
        Utils.async(() -> {
            try {
                send(message);
            } catch (Throwable e) {
                EventBus.push(e);
            }
        });
    }

    @Override
    public void send(String message) {
        synchronized (this) {
            ByteBuffer buf = ProtocolManager.encode(Message.wrap(message));
            real.send(buf);
        }
    }

    @Override
    public void send(Message message) {
        super.send(message);

        synchronized (this) {
            ByteBuffer buf = ProtocolManager.encode(message);
            real.send(buf);
        }
    }

    @Override
    public void close() throws IOException {
        if(real == null){
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

        return real.isOpen();
    }

    @Override
    public boolean isSecure() {
        return real.hasSSLSupport();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return real.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return real.getLocalSocketAddress();
    }

    @Override
    public void setAttachment(Object obj) {
        real.setAttachment(obj);
    }

    @Override
    public <T> T getAttachment() {
        return real.getAttachment();
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
