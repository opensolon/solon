package org.noear.solon.net.stomp;

import org.noear.solon.net.stomp.broker.impl.SubscriptionInfo;
import org.noear.solon.net.websocket.WebSocket;

/**
 * @author noear
 * @since 3.0
 */
public interface StompSession {
    WebSocket getSocket();

    String id();

    String name();

    void nameAs(String name);

    SubscriptionInfo getSubscription(String destination);
}
