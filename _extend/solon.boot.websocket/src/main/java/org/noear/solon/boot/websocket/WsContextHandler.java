package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.XApp;
import org.noear.solon.core.SocketMessage;
import org.noear.solon.core.XEventBus;

public class WsContextHandler {

    public void handle(WebSocket socket, byte[] message, boolean messageIsString) {
        try {
            SocketMessage request = SocketMessageUtils.wrap(null, socket.getResourceDescriptor(), message);

            WsContext context = new WsContext(socket, request, messageIsString);

            XApp.global().tryHandle(context);

            context.commit();
        } catch (Exception ex) {
            XEventBus.push(ex);
        }
    }
}
