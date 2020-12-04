package org.noear.solon.extend.socketd.client.websocket;

import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionBase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

public class _SocketSession extends SessionBase {
    Object connector;
    boolean autoReconnect;

    public _SocketSession(Object connector, boolean autoReconnect) {
        this.connector = connector;
        this.autoReconnect = autoReconnect;
    }

    @Override
    public Object real() {
        return null;
    }

    @Override
    public String sessionId() {
        return null;
    }

    @Override
    public MethodType method() {
        return null;
    }

    @Override
    public String path() {
        return null;
    }

    @Override
    public void send(String message) {

    }

    @Override
    public void send(byte[] message) {

    }

    @Override
    public void send(Message message) {

    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public void setAttachment(Object obj) {

    }

    @Override
    public <T> T getAttachment() {
        return null;
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return null;
    }
}
