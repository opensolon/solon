package org.noear.solon.socketd.client.smartsocket;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.Connector;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionBase;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.*;

public class AioSocketSession extends SessionBase {
    public static final Map<AioSession, Session> sessions = new HashMap<>();

    public static Session get(AioSession real) {
        Session tmp = sessions.get(real);
        if (tmp == null) {
            synchronized (real) {
                tmp = sessions.get(real);
                if (tmp == null) {
                    tmp = new AioSocketSession(real);
                    sessions.put(real, tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(AioSession real) {
        sessions.remove(real);
    }



    private final String _sessionId = Utils.guid();
    private AioSession real;

    public AioSocketSession(AioSession real) {
        this.real = real;
    }


    Connector<AioSession> connector;
    boolean autoReconnect;

    public AioSocketSession(Connector<AioSession> connector) {
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
                if (real.isInvalid()) {
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
    public void sendAsync(String message) {
        Utils.pools.submit(() -> {
            try {
                send(message);
            } catch (Throwable e) {
                EventBus.push(e);
            }
        });
    }

    @Override
    public void sendAsync(Message message) {
        Utils.pools.submit(() -> {
            try {
                send(message);
            } catch (Throwable e) {
                EventBus.push(e);
            }
        });
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
                send0(message);
            }
        } catch (ClosedChannelException e) {
            if (autoReconnect) {
                real = null;
            } else {
                throw new RuntimeException(e);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void send0(Message message) throws IOException {
        if (message == null) {
            return;
        }

        ByteBuffer buf = ProtocolManager.encode(message);
        real.writeBuffer().writeAndFlush(buf.array());
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

        return real.isInvalid() == false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        try {
            return real.getRemoteAddress();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        try {
            return real.getLocalAddress();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
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
        AioSocketSession that = (AioSocketSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
