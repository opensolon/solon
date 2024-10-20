package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.StompSession;
import org.noear.solon.net.websocket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stomp 会话实现
 *
 * @author noear
 * @since 3.0
 */
public class StompSessionImpl implements StompSession {
    public static StompSessionImpl of(WebSocket socket) {
        StompSessionImpl tmp = socket.attr("SESSION");
        if (tmp == null) {
            tmp = new StompSessionImpl(socket);
            socket.attr("SESSION", tmp);
        }

        return tmp;
    }

    private final WebSocket socket;
    private final Set<Subscription> subscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>());


    private StompSessionImpl(WebSocket socket) {
        this.socket = socket;
    }

    public WebSocket getSocket() {
        return socket;
    }

    @Override
    public String id() {
        return socket.id();
    }

    @Override
    public String name() {
        return socket.name();
    }

    @Override
    public void nameAs(String name) {
        socket.nameAs(name);
    }


    @Override
    public String param(String name) {
        return socket.param(name);
    }

    @Override
    public String paramOrDefault(String name, String def) {
        return socket.paramOrDefault(name, def);
    }

    @Override
    public InetSocketAddress remoteAddress() throws IOException {
        return socket.remoteAddress();
    }

    @Override
    public void send(Frame frame) {
        assert frame != null;

        if (socket.isValid()) {
            String frameStr = StompBrokerMedia.codec.encode(frame);
            socket.send(ByteBuffer.wrap(frameStr.getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Override
    public void close() {
        socket.close();
    }

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public Subscription getSubscription(String destination) {
        for (Subscription sub : subscriptions) {
            if (sub.matches(destination)) {
                return sub;
            }
        }

        return null;
    }
}
