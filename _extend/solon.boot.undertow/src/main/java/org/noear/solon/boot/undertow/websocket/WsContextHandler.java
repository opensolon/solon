package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import org.noear.solon.XApp;
import org.noear.solon.core.XEventBus;
import org.noear.solonx.socket.api.XSocketMessage;

public class WsContextHandler {

    public void handle(WebSocketChannel socket, XSocketMessage message, boolean messageIsString) {
        try {
            WsContext context = new WsContext(socket, message, messageIsString);

            XApp.global().tryHandle(context);

            if (context.getHandled()) {
                context.commit();
            }
        } catch (Exception ex) {
            XEventBus.push(ex);
        }
    }
}