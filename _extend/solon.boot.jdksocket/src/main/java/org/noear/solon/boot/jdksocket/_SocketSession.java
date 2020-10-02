package org.noear.solon.boot.jdksocket;

import org.noear.solon.extend.socketapi.XSession;
import org.noear.solon.extend.socketapi.XSignal;
import org.noear.solon.extend.socketapi.XSocketMessage;
import org.noear.solon.extend.socketapi.XSocketMessageUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class _SocketSession implements XSession {
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

    @Override
    public XSignal signal() {
        return XSignal.SOCKET;
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

    public void send(XSocketMessage message) {
        try {
            //
            // 转包为XSocketMessage，再转byte[]
            //
            byte[] bytes = XSocketMessageUtils.encode(message).array();

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
    public String resourceDescriptor() {
        return "";
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
    public static XSocketMessage receive(Socket socket, SocketProtocol protocol) {
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
