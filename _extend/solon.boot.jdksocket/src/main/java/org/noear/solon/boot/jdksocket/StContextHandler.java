package org.noear.solon.boot.jdksocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XEventBus;
import org.noear.solonx.socket.api.XSocketMessage;

public class StContextHandler {

    public void handle(SocketSession session, XSocketMessage message) {
        if (message == null) {
            return;
        }

        try {
            handleDo(session, message);
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            XEventBus.push(ex);
        }
    }

    private void handleDo(SocketSession session, XSocketMessage message) {
        StContext context = new StContext(session, message);

        try {
            XApp.global().tryHandle(context);

            context.commit();
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }
}
