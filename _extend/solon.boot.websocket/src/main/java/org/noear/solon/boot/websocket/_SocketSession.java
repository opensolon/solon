package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.extend.socketapi.XSession;
import org.noear.solon.extend.socketapi.XSignal;
import org.noear.solon.extend.socketapi.XMessage;

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
    public XSignal signal() {
        return XSignal.WEBSOCKET;
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

    private String _resourceDescriptor;
    @Override
    public String resourceDescriptor() {
        if(_resourceDescriptor == null) {
            _resourceDescriptor = real.getResourceDescriptor();
        }

        return _resourceDescriptor;
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
