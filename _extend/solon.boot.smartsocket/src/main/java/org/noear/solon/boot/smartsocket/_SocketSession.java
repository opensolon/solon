package org.noear.solon.boot.smartsocket;

import org.noear.solonx.socket.api.XSession;
import org.noear.solonx.socket.api.XSocketMessage;
import org.noear.solonx.socket.api.XSocketMessageUtils;
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
    public Object real() {
        return real;
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
        send(XSocketMessage.wrap(message));
    }

    @Override
    public void send(XSocketMessage message) {
        try {
            //
            // 转包为XSocketMessage，再转byte[]
            //
            byte[] bytes = XSocketMessageUtils.encode(message).array();

            real.writeBuffer().writeAndFlush(bytes);
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
    public boolean isValid() {
        return real.isInvalid() == false;
    }

    @Override
    public InetSocketAddress getRemoteAddress() throws IOException{
        return real.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() throws IOException{
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
