package org.noear.solon.boot.netty;

import io.netty.channel.Channel;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.MessageWrapper;
import org.noear.solon.extend.socketd.SessionBase;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

class _SocketSession extends SessionBase {
    public static Map<Channel, Session> sessions = new HashMap<>();

    public static Session get(Channel real) {
        Session tmp = sessions.get(real);
        if (tmp == null) {
            synchronized (real) {
                tmp = sessions.get(real);
                if (tmp == null) {
                    tmp = new _SocketSession(real);
                    sessions.put(real, tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(Channel real) {
        sessions.remove(real);
    }

    Channel real;

    public _SocketSession(Channel real) {
        this.real = real;
    }


    NioConnector connector;
    boolean autoReconnect;

    public _SocketSession(NioConnector connector, boolean autoReconnect) {
        this.connector = connector;
        this.autoReconnect = autoReconnect;
    }

    /**
     * @return 是否为新链接
     */
    private boolean prepareNew() throws IOException {
        if (real == null) {
            real = connector.connect(this);
            onOpen();

            return true;
        } else {
            if (autoReconnect) {
                if (real.isActive() == false) {
                    real = connector.connect(this);
                    onOpen();

                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public Object real() {
        return real;
    }

    private String _sessionId = Utils.guid();

    @Override
    public String sessionId() {
        return _sessionId;
    }

    @Override
    public MethodType method() {
        return MethodType.SOCKET;
    }

    @Override
    public URI uri() {
        if(connector == null){
            return null;
        }else {
            return connector.getUri();
        }
    }

    @Override
    public String path() {
        if (connector == null) {
            return "";
        } else {
            return connector.getUri().getPath();
        }
    }

    @Override
    public void send(String message) {
        send(message.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void send(byte[] message) {
        send(MessageWrapper.wrap(message));
    }

    @Override
    public void send(Message message) {
        try {
            super.send(message);

            synchronized (this) {
                if (prepareNew()) {
                    send0(handshakeMessage);
                }

                //
                // 转包为Message，再转byte[]
                //
                //byte[] bytes = MessageUtils.encode(message).array();

                send0(message);
            }
        } catch (RuntimeException ex) {
            Throwable ex2 = Utils.throwableUnwrap(ex);
            if (ex2 instanceof ConnectException) {
                if (autoReconnect) {
                    real = null;
                }
            }
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void send0(Message message) throws IOException {
        if (message == null) {
            return;
        }

        real.writeAndFlush(message);
    }


    @Override
    public void close() throws IOException {
        real.close();
        sessions.remove(real);
    }

    @Override
    public boolean isValid() {
        return real.isActive();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        try {
            return (InetSocketAddress) real.remoteAddress();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        try {
            return (InetSocketAddress) real.localAddress();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private Object attachment;

    @Override
    public void setAttachment(Object obj) {
        attachment = obj;
    }

    @Override
    public <T> T getAttachment() {
        return (T) attachment;
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        _SocketSession that = (_SocketSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
