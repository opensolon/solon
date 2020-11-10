package org.noear.solon.boot.jdksocket;

import org.noear.solon.XUtil;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XSession;
import org.noear.solon.core.XMessage;
import org.noear.solon.ext.LinkedCaseInsensitiveMap;
import org.noear.solon.extend.xsocket.XMessageUtils;
import org.noear.solon.extend.xsocket.XSessionBase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class _SocketSession extends XSessionBase {
    public static Map<Socket, XSession> sessions = new HashMap<>();

    public static XSession get(Socket real) {
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

    public static void remove(Socket real) {
        sessions.remove(real);
    }


    Socket real;

    public _SocketSession(Socket real) {
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

    public void send(XMessage message) {
        try {
            //
            // 转包为XSocketMessage，再转byte[]
            //
            byte[] bytes = XMessageUtils.encode(message).array();

            real.getOutputStream().write(bytes);
            real.getOutputStream().flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    @Override
    public void close() throws IOException {
        synchronized (real) {
            real.shutdownInput();
            real.shutdownOutput();
            real.close();

            sessions.remove(real);
        }
    }

    @Override
    public boolean isValid() {
        return real.isConnected();
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress()  {
        return (InetSocketAddress) real.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) real.getLocalSocketAddress();
    }

    private Object attachment;

    @Override
    public void setAttachment(Object obj) {
        attachment = obj;
    }

    @Override
    public <T> T getAttachment() {
        return (T) attachment;
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

    /**
     * 接收数据
     */
    public static XMessage receive(Socket socket, SocketProtocol protocol) {
        try {
            return protocol.decode(socket.getInputStream());
        } catch (SocketException ex) {
            return null;
        } catch (Throwable ex) {
            System.out.println("Decoding failure::");
            ex.printStackTrace();
            return null;
        }
    }
}
