package org.noear.solon.boot.undertow.websocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XEventBus;
import org.noear.solonx.socket.api.XSession;
import org.noear.solonx.socket.api.XSocketMessage;

public class WsContextHandler {

    public void handle(XSession session, XSocketMessage message, boolean messageIsString) {
        try {
            WsContext context = new WsContext(session, message, messageIsString);

            XApp.global().tryHandle(context);

            if (context.getHandled() && context.status() != 404) {
                context.commit();
            }
        } catch (Exception ex) {
            XEventBus.push(ex);
        }
    }
}