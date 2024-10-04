package org.noear.solon.net.stomp.handle;

import org.noear.solon.Solon;
import org.noear.solon.core.handle.ContextEmpty;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.util.KeyValue;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.StompMessageSender;
import org.noear.solon.net.stomp.impl.Headers;
import org.noear.solon.net.stomp.impl.DestinationInfo;
import org.noear.solon.net.websocket.WebSocket;

/**
 * @author noear
 * @since 3.0
 */
public class StompContext extends ContextEmpty {
    private WebSocket session;
    private Message message;
    private DestinationInfo destinationInfo;
    private StompMessageSender messageSender;

    public StompContext(WebSocket session, Message message, DestinationInfo destinationInfo) {
        this.session = session;
        this.message = message;
        this.destinationInfo = destinationInfo;
        this.messageSender = session.attr("STOMP_MESSAGE_SENDER");

        for (KeyValue<String> kv : message.getHeaderAll()) {
            headerMap().add(kv.getKey(), kv.getValue());
        }

        attrSet(org.noear.solon.core.Constants.ATTR_RETURN_HANDLER, StompReturnHandler.getInstance());
    }

    public WebSocket getSession() {
        return session;
    }

    public Message getMessage() {
        return message;
    }

    public StompMessageSender getMessageSender() {
        return messageSender;
    }

    @Override
    public String sessionId() {
        return session.id();
    }

    @Override
    public String contentType() {
        return message.getHeader(Headers.CONTENT_TYPE);
    }

    @Override
    public String path() {
        return destinationInfo.getDestination();
    }

    @Override
    public Object pull(Class<?> clz) {
        if (Message.class.isAssignableFrom(clz)) {
            return message;
        }

        if (WebSocket.class.isAssignableFrom(clz)) {
            return session;
        }

        return null;
    }

    public void tryHandle() throws Throwable {
        Handler handler = Solon.app().router().matchMain(this);
        if (handler != null) {
            handler.handle(this);
        } else {
            throw new IllegalStateException("No mapping registration");
        }
    }
}