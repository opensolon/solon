package org.noear.solon.boot.rsocket;

import io.rsocket.RSocket;
import io.rsocket.util.DefaultPayload;
import org.noear.solon.XUtil;
import org.noear.solon.core.*;
import org.noear.solon.extend.xsocket.XListenerProxy;
import org.noear.solon.extend.xsocket.XMessageUtils;
import org.noear.solon.extend.xsocket.XSessionBase;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;

public class _SocketSession extends XSessionBase {
    public static Map<RSocket, XSession> sessions = new HashMap<>();

    public static XSession get(RSocket real) {
        XSession tmp = sessions.get(real);
        if (tmp == null) {
            synchronized (real) {
                tmp = sessions.get(real);
                if (tmp == null) {
                    tmp = new _SocketSession(real);
                    sessions.put(real, tmp);

                    //算第一次
                    XListenerProxy.getGlobal().onOpen(tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(Socket real) {
        sessions.remove(real);
    }

    RSocket real;

    public _SocketSession(RSocket real) {
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
        //
        // 转包为XSocketMessage，再转byte[]
        //
        ByteBuffer buffer = XMessageUtils.encode(message);

        real.requestResponse(DefaultPayload.create(buffer));
    }

    @Override
    public XMessage sendAndResponse(XMessage message) {
        ByteBuffer buffer = XMessageUtils.encode(message);

        ValHolder<XMessage> holder = new ValHolder<>();

        real.requestResponse(DefaultPayload.create(buffer))
                .map(d -> XMessageUtils.decode(d.getData()))
                .doOnNext(m -> holder.value = m)
                .block();

        return holder.value;
    }

    @Override
    public void close() throws IOException {
        synchronized (real) {
            real.dispose();

            sessions.remove(real);
        }
    }

    @Override
    public boolean isValid() {
        return real.isDisposed() == false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress()  {
        return null;
        //return (InetSocketAddress) real.getRemoteSocketAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
        //return (InetSocketAddress) real.getLocalSocketAddress();
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
