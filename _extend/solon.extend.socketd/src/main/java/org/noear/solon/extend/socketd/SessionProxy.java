package org.noear.solon.extend.socketd;

import org.noear.solon.core.handle.MethodType;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collection;
import java.util.function.BiConsumer;

public class SessionProxy implements Session {
    Session session;

    public SessionProxy(Session session) {
        this.session = session;
    }

    @Override
    public Object real() {
        return session.real();
    }

    @Override
    public String sessionId() {
        return session.sessionId();
    }

    @Override
    public MethodType method() {
        return session.method();
    }

    @Override
    public URI uri() {
        return session.uri();
    }

    @Override
    public String path() {
        return session.path();
    }

    @Override
    public void send(String message) {
        session.send(message);
    }

    @Override
    public void send(byte[] message) {
        session.send(message);
    }

    @Override
    public void send(Message message) {
        session.send(message);
    }

    @Override
    public Message sendAndResponse(Message message) {
        return session.sendAndResponse(message);
    }

    @Override
    public void sendAndCallback(Message message, BiConsumer<Message, Throwable> callback) {
        session.sendAndCallback(message, callback);
    }

    @Override
    public void close() throws IOException {
        session.close();
    }

    @Override
    public boolean isValid() {
        return session.isValid();
    }

    @Override
    public boolean isSecure() {
        return session.isSecure();
    }

    @Override
    public void setHandshaked(boolean handshaked) {
        session.setHandshaked(handshaked);
    }

    @Override
    public boolean getHandshaked() {
        return session.getHandshaked();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return session.getRemoteAddress();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return session.getLocalAddress();
    }

    @Override
    public void setAttachment(Object obj) {
        session.setAttachment(obj);
    }

    @Override
    public <T> T getAttachment() {
        return session.getAttachment();
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return session.getOpenSessions();
    }

    @Override
    public void sendHeartbeat() {
        session.sendHeartbeat();
    }

    @Override
    public void sendHeartbeatAuto(int intervalSeconds) {
        session.sendHeartbeatAuto(intervalSeconds);
    }

    @Override
    public void sendHandshake(Message message) {
        session.sendHandshake(message);
    }

    @Override
    public Message sendHandshakeAndResponse(Message message) {
        return session.sendHandshakeAndResponse(message);
    }
}
