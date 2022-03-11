package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.ProtocolManager;
import org.noear.solon.socketd.SessionBase;
import org.smartboot.http.server.WebSocketRequest;
import org.smartboot.http.server.WebSocketResponse;
import org.smartboot.http.server.impl.WebSocketRequestImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.*;

public class _SocketServerSession extends SessionBase {
    public static Map<WebSocketRequest, _SocketServerSession> sessions = new HashMap<>();

    public static _SocketServerSession get(WebSocketRequest req, WebSocketResponse res) {
        _SocketServerSession tmp = sessions.get(req);
        if (tmp == null) {
            synchronized (req) {
                tmp = sessions.get(req);
                if (tmp == null) {
                    tmp = new _SocketServerSession(req, res);
                    sessions.put(req, tmp);
                }
            }
        }

        return tmp;
    }

    public static _SocketServerSession getOnly(WebSocketRequest real) {
        return sessions.get(real);
    }

    public static void remove(WebSocketRequest real) {
        sessions.remove(real);
    }


    WebSocketRequest request;
    WebSocketResponse response;

    public _SocketServerSession(WebSocketRequest request, WebSocketResponse response) {
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

    private URI _uri;

    @Override
    public URI uri() {
        if (_uri == null) {
            _uri = URI.create(((WebSocketRequestImpl) request).getRequestURL());
        }

        return _uri;
    }

    private String _path;

    @Override
    public String path() {
        if (_path == null) {
            _path = uri().getPath();
        }

        return _path;
    }

    @Override
    public void send(String message) {
        if (Solon.global().enableWebSocketD()) {
            sendBuffer(ProtocolManager.encode(Message.wrap(message)));
        } else {
            response.sendTextMessage(message);
            response.flush();
        }
    }

    @Override
    public void send(Message message) {
        super.send(message);

        if (Solon.global().enableWebSocketD()) {
            sendBuffer(ProtocolManager.encode(message));
        } else {
            if (message.isString()) {
                send(message.bodyAsString());
            } else {
                sendBytes(message.body());
            }
        }
    }

    private void sendBuffer(ByteBuffer buffer) {
        if (buffer != null) {
            sendBytes(buffer.array());
        }
    }

    private void sendBytes(byte[] message) {
        response.sendBinaryMessage(message);
        response.flush();
    }

    private boolean isOpen = true;

    @Override
    public void close() throws IOException {
        isOpen = false;
        response.close();
        sessions.remove(request);
    }

    protected void onClose() {
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
    public Collection<Session> getOpenSessions() {
        return new ArrayList<>(sessions.values());
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        _SocketServerSession that = (_SocketServerSession) o;
        return Objects.equals(request, that.request);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request);
    }
}
