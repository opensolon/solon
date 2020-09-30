package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.XApp;
import org.noear.solonx.socket.api.XSocketMessage;
import org.noear.solon.core.XEventBus;

public class WsContextHandler {

    public void handle(WebSocket socket, byte[] message, boolean messageIsString) {
        try {
            XSocketMessage request = XSocketMessage.wrap(null, socket.getResourceDescriptor(), message);

            WsContext context = new WsContext(socket, request, messageIsString);

            XApp.global().tryHandle(context);

            if (context.getHandled()) {
                context.commit();
            }
        } catch (Exception ex) {
            XEventBus.push(ex);
        }
    }
}
