package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.MessageSession;
import org.noear.solon.extend.xsocket.MessageSessionBase;
import org.smartboot.http.WebSocketRequest;
import org.smartboot.http.WebSocketResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

public class _SocketSession extends MessageSessionBase {
    public static Map<WebSocketRequest, _SocketSession> sessions = new HashMap<>();

    public static _SocketSession get(WebSocketRequest req, WebSocketResponse res) {
        _SocketSession tmp = sessions.get(req);
        if (tmp == null) {
            synchronized (req) {
                tmp = sessions.get(req);
                if (tmp == null) {
                    tmp = new _SocketSession(req, res);
                    sessions.put(req, tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(WebSocketRequest real) {
        sessions.remove(real);
    }


    WebSocketRequest request;
    WebSocketResponse response;

    public _SocketSession(WebSocketRequest request, WebSocketResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public Object real() {
        return request;
    }

    private String _sessionId = Utils.guid();
    @Override
    public String sessionId() {
        return _sessionId;
    }

    @Override
    public MethodType method() {
        return MethodType.WEBSOCKET;
    }

    private String _path;

    @Override
    public String path() {
        if (_path == null) {
            _path = URI.create(request.getRequestURI()).getPath();
        }

        return _path;
    }

    @Override
    public void send(String message) {
        response.sendTextMessage(message);
    }

    @Override
    public void send(byte[] message) {
        ByteBuffer buf = ByteBuffer.wrap(message);
        response.sendBinaryMessage(buf.array());
    }

    @Override
    public void send(Message message) {
        send(message.content());
    }


    private boolean isOpen = true;

    @Override
    public void close() throws IOException {
        isOpen = false;
        response.close();
        sessions.remove(request);
    }

    protected void onClose(){
        isOpen = false;
    }

    @Override
    public boolean isValid() {
        return isOpen;
    }

    @Override
    public boolean isSecure() {
        return request.getRequestURI().startsWith("wss:");
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return request.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return request.getLocalAddress();
    }

    private Object _attachment;
    @Override
    public void setAttachment(Object obj) {
        _attachment = obj;
    }

    @Override
    public <T> T getAttachment() {
        return (T) _attachment;
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
        return Objects.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request);
    }
}
