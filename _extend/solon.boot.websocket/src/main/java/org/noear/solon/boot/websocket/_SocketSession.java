package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.core.XMethod;
import org.noear.solon.xsocket.XSession;
import org.noear.solon.xsocket.XMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class _SocketSession implements XSession {
    public static Map<WebSocket, XSession> sessions = new HashMap<>();
    public static XSession get(WebSocket real) {
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

    public static void remove(WebSocket real){
        sessions.remove(real);
    }



    WebSocket real;
    public _SocketSession(WebSocket real){
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
            _path = real.getResourceDescriptor();
        }

        return _path;
    }

    @Override
    public void send(String message) {
        real.send(message);
    }

    @Override
    public void send(byte[] message) {
        real.send(message);
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
        return real.hasSSLSupport();
    }

    @Override
    public InetSocketAddress getRemoteAddress(){
        return real.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress(){
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
