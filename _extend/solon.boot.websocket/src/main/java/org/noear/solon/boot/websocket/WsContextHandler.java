package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.XApp;
import org.noear.solonx.socket.api.XSocketMessage;
import org.noear.solon.core.XEventBus;

public class WsContextHandler {

    public void handle(WebSocket socket, XSocketMessage message, boolean messageIsString) {
        try {
            WsContext context = new WsContext(socket, message, messageIsString);

            XApp.global().tryHandle(context);

            if (context.getHandled() && context.status() != 404) {
                context.commit();
            }
        } catch (Exception ex) {
            XEventBus.push(ex);
        }
    }
}