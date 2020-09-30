package org.noear.solon.boot.smartsocket;

import org.noear.solon.api.socket.XSession;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.*;

public class _SocketSession implements XSession {
    public static Map<AioSession, XSession> sessions = new HashMap<>();
    public static XSession get(AioSession real) {
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

    public static void remove(AioSession real){
        sessions.remove(real);
    }

    AioSession real;
    public _SocketSession(AioSession real){
        this.real = real;
    }

    @Override
    public void send(String message) {
        try {
            send(message.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void send(byte[] message) {
        try {
            real.writeBuffer().writeAndFlush(message);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() throws IOException {
        real.close();
        sessions.remove(real);
    }

    @Override
    public boolean isOpen() {
        return real.isInvalid() == false;
    }

    @Override
    public boolean isClosed() {
        return real.isInvalid();
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() throws IOException{
        return real.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() throws IOException{
        return real.getLocalAddress();
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
