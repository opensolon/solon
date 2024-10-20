package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.StompSession;
import org.noear.solon.net.websocket.WebSocket;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
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
    private final Set<SubscriptionInfo> subscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>());


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

    public void addSubscription(SubscriptionInfo subscription) {
        subscriptions.add(subscription);
    }

    public SubscriptionInfo getSubscription(String destination) {
        for (SubscriptionInfo sub : subscriptions) {
            if (sub.matches(destination)) {
                return sub;
            }
        }

        return null;
    }
}
