package org.noear.solon.boot.jetty.websocket;

import org.noear.solon.Utils;
import org.noear.solon.core.handler.MethodType;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.xsocket.XSessionBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class _SocketSession extends XSessionBase {
    public static Map<org.eclipse.jetty.websocket.api.Session, MessageSession> sessions = new HashMap<>();
    public static MessageSession get(org.eclipse.jetty.websocket.api.Session real) {
        MessageSession tmp = sessions.get(real);
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

    public static void remove(org.eclipse.jetty.websocket.api.Session real){
        sessions.remove(real);
    }



    org.eclipse.jetty.websocket.api.Session real;
    public _SocketSession(org.eclipse.jetty.websocket.api.Session real){
        this.real = real;
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

    private String _path;
    @Override
    public String path() {
        if(_path == null) {
            _path = real.getUpgradeRequest().getRequestURI().getPath();
        }

        return _path;
    }

    @Override
    public void send(String message) {
        try {
            real.getRemote().sendString(message);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void send(byte[] message) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(message);
            real.getRemote().sendBytes(buf);
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void send(Message message) {
        send(message.content());
    }


    private boolean _open = true;
    @Override
    public void close() throws IOException {
        _open = false; //jetty 的 close 不及时
        real.close();
        sessions.remove(real);
    }

    @Override
    public boolean isValid() {
        return _open && real.isOpen();
    }

    @Override
    public boolean isSecure() {
        return real.isSecure();
    }

    @Override
    public InetSocketAddress getRemoteAddress(){
        return real.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(){
        return real.getLocalAddress();
    }

    private Object attachment;

    @Override
    public void setAttachment(Object obj) {
        attachment = obj;
    }

    @Override
    public <T> T getAttachment() {
        return (T)attachment;
    }

    @Override
    public Collection<MessageSession> getOpenSessions() {
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
