package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.noear.solon.core.XMethod;
import org.noear.solon.xsocket.XSession;
import org.noear.solon.xsocket.XMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class _SocketSession implements XSession {
    public static Map<Session, XSession> sessions = new HashMap<>();
    public static XSession get(Session real) {
        XSession tmp = sessions.get(real);
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

    public static void remove(Session real){
        sessions.remove(real);
    }



    Session real;
    public _SocketSession(Session real){
        this.real = real;
    }

    @Override
    public Object real() {
        return real;
    }

    @Override
    public XMethod method() {
        return XMethod.WEBSOCKET;
    }

    private String _path;
    @Override
    public String path() {
        if(_path == null) {
            _path = real.getUpgradeRequest().getOrigin();
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
    public void send(XMessage message) {
        send(message.content());
    }

    @Override
    public void close() throws IOException {
        real.close();
        sessions.remove(real);
    }

    @Override
    public boolean isValid() {
        return real.isOpen();
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
    public Collection<XSession> getOpenSessions() {
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
