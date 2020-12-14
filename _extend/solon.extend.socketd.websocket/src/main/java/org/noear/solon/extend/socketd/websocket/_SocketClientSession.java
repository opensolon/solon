package org.noear.solon.extend.socketd.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.extend.socketd.Connector;
import org.noear.solon.extend.socketd.MessageUtils;
import org.noear.solon.extend.socketd.SessionBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class _SocketClientSession extends SessionBase {

    WebSocket real;

    final Connector<WebSocket> connector;
    final boolean autoReconnect;

    public _SocketClientSession(Connector<WebSocket> connector) {
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

    private String _sessionId = Utils.guid();

    @Override
    public String sessionId() {
        return _sessionId;
    }

    @Override
    public MethodType method() {
        return MethodType.WEBSOCKET;
    }

    @Override
    public URI uri() {
        return connector.getUri();
    }

    private String _path;

    @Override
    public String path() {
        if (_path == null) {
            _path = connector.getUri().getPath();
        }

        return _path;
    }

    @Override
    public void send(String message) {
        if (Solon.global().enableWebSocketD()) {
            sendD(MessageUtils.wrap(message.getBytes(StandardCharsets.UTF_8)));
        }else{
            sendW(() -> real.send(message));
        }
    }


    @Override
    public void send(byte[] message) {
        if (Solon.global().enableWebSocketD()) {
            sendD(MessageUtils.wrap(message));
        } else {
            sendW(() -> real.send(message));
        }
    }

    @Override
    public void send(Message message) {
        if (Solon.global().enableWebSocketD()) {
            sendD(message);
        } else {
            sendW(() -> real.send(message.body()));
        }
    }

    private void sendW(RunnableEx runnable){
        try {
            synchronized (this) {
                prepareNew();

                //
                // 转包为Message，再转byte[]
                //
                runnable.run();
            }
        } catch (SocketException ex) {
            if (autoReconnect) {
                real = null;
            }

            throw new RuntimeException(ex);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    private void sendD(Message message) {
        try {
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

        ByteBuffer buffer = MessageUtils.encode(message);

        if (buffer != null) {
            real.send(buffer.array());
        }
    }


    @Override
    public void close() throws IOException {
        real.close();
    }

    @Override
    public boolean isValid() {
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
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        _SocketClientSession that = (_SocketClientSession) o;
        return Objects.equals(real, that.real);
    }

    @Override
    public int hashCode() {
        return Objects.hash(real);
    }
}
