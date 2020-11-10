package org.noear.solon.boot.smartsocket;

import org.noear.solon.XUtil;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XSession;
import org.noear.solon.core.XMessage;
import org.noear.solon.extend.xsocket.XMessageUtils;
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

    private String _sessionId = XUtil.guid();
    @Override
    public String sessionId() {
        return _sessionId;
    }

    @Override
    public XMethod method() {
        return XMethod.SOCKET;
    }

    @Override
    public String path() {
        return "";
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
        send(XMessage.wrap(message));
    }

    @Override
    public void send(XMessage message) {
        try {
            //
            // 转包为XSocketMessage，再转byte[]
            //
            byte[] bytes = XMessageUtils.encode(message).array();

            real.writeBuffer().writeAndFlush(bytes);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public XMessage sendAndResponse(XMessage message) {
        return null;
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
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        try {
            return real.getRemoteAddress();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        try {
            return real.getLocalAddress();
        } catch (Throwable ex) {
            throw new RuntimeException(ex);
        }
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
