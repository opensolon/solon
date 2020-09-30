package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.XApp;
import org.noear.solon.api.socket.SocketMessage;
import org.noear.solon.core.XEventBus;

public class WsContextHandler {

    public void handle(WebSocket socket, byte[] message, boolean messageIsString) {
        try {
            SocketMessage request = SocketMessage.wrap(null, socket.getResourceDescriptor(), message);

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
