package org.noear.solon.socketd.client.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.ext.RunnableEx;
import org.noear.solon.socketd.Connector;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionBase;
import org.noear.solon.socketd.SessionFlag;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class _SocketClientSession extends SessionBase {



    private final String _sessionId = Utils.guid();
    public WebSocket real;

    final Connector<WebSocket> connector;
    final boolean autoReconnect;

    public _SocketClientSession(Connector<WebSocket> connector) {
        this.connector = connector;
        this.autoReconnect = connector.autoReconnect();
    }

    private boolean isWebSocketD() {
        return Solon.app().enableWebSocketD() || flag() == SessionFlag.socketd;
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
        return MethodType.WEBSOCKET;
    }

    @Override
    public URI uri() {
        return connector.uri();
    }

    private String _path;

    @Override
    public String path() {
        if (_path == null) {
            _path = connector.uri().getPath();
        }

        return _path;
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
        synchronized (this) {
            try {
                if (isWebSocketD()) {
                    sendD(Message.wrap(message.getBytes(StandardCharsets.UTF_8)));
                } else {
                    sendW(() -> real.send(message));
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
    }

    @Override
    public void send(Message message) {
        super.send(message);

        synchronized (this) {
            try {
                if (isWebSocketD()) {
                    sendD(message);
                } else {
                    if (message.isString()) {
                        send(message.bodyAsString());
                    } else {
                        sendW(() -> real.send(message.body()));
                    }
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
    }

    private void sendW(RunnableEx runnable) throws Throwable {
        prepareNew();

        //
        // 转包为Message，再转byte[]
        //
        runnable.run();
    }

    private void sendD(Message message) throws IOException{
        if (prepareNew()) {
            send0(handshakeMessage);
        }

        //
        // 转包为Message，再转byte[]
        //
        send0(message);
    }

    private void send0(Message message) throws IOException {
        if (message == null) {
            return;
        }

        ByteBuffer buf = ProtocolManager.encode(message);
        real.send(buf.array());
    }


    @Override
    public void close() throws IOException {
        if (real == null) {
            return;
        }

        real.close();
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
