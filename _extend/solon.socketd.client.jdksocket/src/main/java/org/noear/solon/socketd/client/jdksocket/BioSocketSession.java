package org.noear.solon.socketd.client.jdksocket;

import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.Connector;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

/**
 *
 * <pre><code>
 * public void test() throws Throwable{
 *     String root = "tcp://localhost:" + (20000 + Solon.global().port());
 *     Message message =  Message.wrap(root + "/demog/中文/1", "Hello 世界!".getBytes());
 *
 *     Socket socket = new Socket("localhost", Solon.global().port() + 20000);
 *
 *     Session session = _SocketSession.get(socket);
 *     Message rst = session.sendAndResponse(message);
 *
 *     System.out.println(rst.toString());
 *
 *     assert "我收到了：Hello 世界!".equals(rst.toString());
 * }
 * </code></pre>
 * */
public class BioSocketSession extends SessionBase {
    public static final Map<Socket, Session> sessions = new HashMap<>();

    public static Session get(Socket real) {
        Session tmp = sessions.get(real);
        if (tmp == null) {
            synchronized (real) {
                tmp = sessions.get(real);
                if (tmp == null) {
                    tmp = new BioSocketSession(real);
                    sessions.put(real, tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(Socket real) {
        sessions.remove(real);
    }


    private final String _sessionId = Utils.guid();
    private Socket real;

    public BioSocketSession(Socket real) {
        this.real = real;
    }

    Connector<Socket> connector;
    boolean autoReconnect;

    public BioSocketSession(Connector<Socket> connector) {
        this.connector = connector;
        this.autoReconnect = connector.autoReconnect();
    }

    /**
     * @return 是否为新链接
     */
    private boolean prepareNew() throws IOException{
        if (real == null) {
            real = connector.open(this);
            onOpen();

            return true;
        } else {
            return false;
        }
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
        } catch (SocketException e) {
            if (autoReconnect) {
                real = null;
            }

            throw new RuntimeException(e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void send0(Message message) throws IOException {
        if (message == null) {
            return;
        }

        ByteBuffer buf = ProtocolManager.encode(message);

        real.getOutputStream().write(buf.array());
        real.getOutputStream().flush();
    }

    @Override
    public void close() throws IOException {
        if (real == null) {
            return;
        }

        real.shutdownInput();
        real.shutdownOutput();
        real.close();

        sessions.remove(real);
    }

    @Override
    public boolean isValid() {
        if(real == null){
            return false;
        }

        return real.isConnected();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) real.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) real.getLocalSocketAddress();
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
        BioSocketSession that = (BioSocketSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
