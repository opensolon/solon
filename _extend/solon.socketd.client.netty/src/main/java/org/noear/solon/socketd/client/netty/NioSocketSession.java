package org.noear.solon.socketd.client.netty;

import io.netty.channel.Channel;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.Connector;
import org.noear.solon.socketd.SessionBase;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;

public class NioSocketSession extends SessionBase {
    protected static final Map<Channel, Session> sessions = new HashMap<>();

    public static Session get(Channel real) {
        Session tmp = sessions.get(real);
        if (tmp == null) {
            synchronized (real) {
                tmp = sessions.get(real);
                if (tmp == null) {
                    tmp = new NioSocketSession(real);
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

    public NioSocketSession(Channel real) {
        this.real = real;
    }


    Connector<Channel> connector;
    boolean autoReconnect;

    public NioSocketSession(Connector<Channel> connector) {
        this.connector = connector;
        this.autoReconnect = connector.autoReconnect();
    }

    /**
     * @return 是否为新链接
     */
    private boolean prepareNew() throws IOException {
        if (real == null) {
            real = connector.open(this);
            onOpen();

            return true;
        } else {
            if (autoReconnect) {
                if (real.isActive() == false) {
                    real = connector.open(this);
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
            return connector.uri();
        }
    }

    @Override
    public String path() {
        if (connector == null) {
            return "";
        } else {
            return connector.uri().getPath();
        }
    }

    @Override
    public void send(String message) {
        send(Message.wrap(message));
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
        return Collections.unmodifiableCollection(sessions.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NioSocketSession that = (NioSocketSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
