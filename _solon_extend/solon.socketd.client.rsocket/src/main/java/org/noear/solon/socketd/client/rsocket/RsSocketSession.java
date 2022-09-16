package org.noear.solon.socketd.client.rsocket;

import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.Connector;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionBase;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

public class RsSocketSession extends SessionBase {
    public static final Map<RSocket, Session> sessions = new HashMap<>();

    public static Session get(RSocket real) {
        Session tmp = sessions.get(real);
        if (tmp == null) {
            synchronized (real) {
                tmp = sessions.get(real);
                if (tmp == null) {
                    tmp = new RsSocketSession(real);
                    sessions.put(real, tmp);

                    //算第一次
                    Solon.app().listener().onOpen(tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(RSocket real) {
        sessions.remove(real);
    }

    private final String _sessionId = Utils.guid();
    private RSocket real;

    public RsSocketSession(RSocket real) {
        this.real = real;
    }

    Connector<RSocket> connector;
    boolean autoReconnect;

    public RsSocketSession(Connector<RSocket> connector) {
        this.connector = connector;
    }

    /**
     * @return 是否为新链接
     */
    private boolean prepareNew() throws IOException {
        if (real == null) {
            real = connector.open(this);
            onOpen();

            return true;
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
        send(Message.wrap(message));
    }


    @Override
    public void send(Message message) {
        super.send(message);


        synchronized (this) {
            try {
                if (prepareNew()) {
                    send0(handshakeMessage);
                }

                send0(message);
            } catch (RuntimeException e) {
                Throwable e2 = Utils.throwableUnwrap(e);
                if (e2 instanceof ConnectException) {
                    if (autoReconnect) {
                        real = null;
                    }
                }
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void send0(Message message) {
        if (message == null) {
            return;
        }

        ByteBuffer buf = ProtocolManager.encode(message);

        real.fireAndForget(DefaultPayload.create(buf))
                .doOnError((err) -> {
                    Solon.app().listener().onError(this, err);
                })
                .subscribe();
    }

    @Override
    public void close() {
        if (real == null) {
            return;
        }

        real.dispose();
        sessions.remove(real);
    }

    @Override
    public boolean isValid() {
        if(real == null){
            return false;
        }

        return real.isDisposed() == false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress()  {
        return null;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
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
        RsSocketSession that = (RsSocketSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
