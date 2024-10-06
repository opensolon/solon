package org.noear.solon.net.stomp.handle;

import org.noear.solon.Utils;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.SimpleStompListener;
import org.noear.solon.net.stomp.StompBrokerSender;
import org.noear.solon.net.stomp.impl.Headers;
import org.noear.solon.net.websocket.WebSocket;

/**
 * @author noear
 * @since 3.0
 */
public class ToHandleStompListener extends SimpleStompListener {
    private final StompBrokerSender messageSender;

    public ToHandleStompListener(StompBrokerSender messageSender) {
        this.messageSender = messageSender;
    }

    @Override
    public void onSend(WebSocket socket, Message message) {
        String destination = message.getHeader(Headers.DESTINATION);

        if(Utils.isNotEmpty(destination)) {
            //同时转发给 Solon Handler 体系
            new StompContext(socket, message, destination, messageSender).tryHandle();;
        }
    }
}
