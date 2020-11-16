package org.noear.solon.boot.smartsocket;

import org.noear.solon.Utils;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.core.message.Message;
import org.noear.solon.extend.xsocket.MessageUtils;
import org.noear.solon.extend.xsocket.MessageSessionBase;
import org.smartboot.socket.transport.AioSession;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.*;

class _SocketSession extends MessageSessionBase {
    public static Map<AioSession, MessageSession> sessions = new HashMap<>();
    public static MessageSession get(AioSession real) {
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
        send(Message.wrap(message));
    }

    @Override
    public void send(Message message) {
        try {
            //
            // 转包为XSocketMessage，再转byte[]
            //
            byte[] bytes = MessageUtils.encode(message).array();

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
