package org.noear.solon.net.stomp.impl;

import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.websocket.WebSocket;

/**
 * @author noear
 * @since 3.0
 */
public class MessageContext extends ContextEmpty {
    private WebSocket webSocket;
    private Message message;
    private DestinationInfo destinationInfo;

    public MessageContext(WebSocket webSocket, Message message, DestinationInfo destinationInfo) {
        this.webSocket = webSocket;
        this.message = message;
        this.destinationInfo = destinationInfo;
    }

    @Override
    public String sessionId() {
        return webSocket.id();
    }

    @Override
    public String path() {
        return destinationInfo.destination;
    }

    @Override
    public Object pull(Class<?> clz) {
        if (Message.class.isAssignableFrom(clz)) {
            return message;
        }

        if (WebSocket.class.isAssignableFrom(clz)) {
            return webSocket;
        }

        return null;
    }
}
