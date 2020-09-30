package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.api.socket.Session;

import java.io.IOException;
import java.util.*;

public class SocketSession implements Session {
    public static Map<WebSocket,Session> sessions = new HashMap<>();
    public static Session get(WebSocket webSocket) {
        Session tmp = sessions.get(webSocket);
        if (tmp == null) {
            synchronized (webSocket) {
                tmp = sessions.get(webSocket);
                if (tmp == null) {
                    tmp = new SocketSession(webSocket);
                    sessions.put(webSocket, tmp);
                }
            }
        }

        return tmp;
    }

    public static void remove(WebSocket webSocket){
        sessions.remove(webSocket);
    }

    WebSocket socket;
    public SocketSession(WebSocket socket){
        this.socket = socket;
    }

    @Override
    public void send(String message) {
        socket.send(message);
    }

    @Override
    public void send(byte[] message) {
        socket.send(message);
    }

    @Override
    public void close() throws IOException {
        socket.close();
        sessions.remove(socket);
    }

    @Override
    public boolean isOpen() {
        return socket.isOpen();
    }

    @Override
    public boolean isClosing() {
        return socket.isClosing();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }

    @Override
    public void setAttachment(Object obj) {
        socket.setAttachment(obj);
    }

    @Override
    public <T> T getAttachment() {
        return socket.getAttachment();
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocketSession that = (SocketSession) o;
        return Objects.equals(socket, that.socket);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket);
    }
}
