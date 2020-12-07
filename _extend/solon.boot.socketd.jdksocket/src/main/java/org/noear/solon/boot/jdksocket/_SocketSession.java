package org.noear.solon.boot.jdksocket;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.socketd.MessageUtils;
import org.noear.solon.extend.socketd.MessageWrapper;
import org.noear.solon.extend.socketd.SessionBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *
 * <pre><code>
 * public void test() throws Throwable{
 *     String root = "tcp://localhost:" + (20000 + Solon.global().port());
 *     XMessage message =  XMessage.wrap(root + "/demog/中文/1", "Hello 世界!".getBytes());
 *
 *     Socket socket = new Socket("localhost", Solon.global().port() + 20000);
 *
 *     XSession session = _SocketSession.get(socket);
 *     XMessage rst = session.sendAndResponse(message);
 *
 *     System.out.println(rst.toString());
 *
 *     assert "我收到了：Hello 世界!".equals(rst.toString());
 * }
 * </code></pre>
 * */
class _SocketSession extends SessionBase {
    public static Map<Socket, Session> sessions = new HashMap<>();

    public static Session get(Socket real) {
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

    public static void remove(Socket real) {
        sessions.remove(real);
    }


    Socket real;

    public _SocketSession(Socket real) {
        this.real = real;
    }

    BioConnector connector;
    boolean autoReconnect;

    public _SocketSession(BioConnector connector, boolean autoReconnect) {
        this.connector = connector;
        this.autoReconnect = autoReconnect;
    }

    /**
     * @return 是否为新链接
     */
    private boolean prepareNew() {
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
        } catch (SocketException ex) {
            if (autoReconnect) {
                real = null;
            }

            throw new RuntimeException(ex);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void send0(Message message) throws IOException {
        if (message == null) {
            return;
        }

        byte[] bytes = MessageUtils.encode(message).array();

        real.getOutputStream().write(bytes);
        real.getOutputStream().flush();
    }

    @Override
    public void close() throws IOException {
        synchronized (real) {
            real.shutdownInput();
            real.shutdownOutput();
            real.close();

            sessions.remove(real);
        }
    }

    @Override
    public boolean isValid() {
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
        _SocketSession that = (_SocketSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
