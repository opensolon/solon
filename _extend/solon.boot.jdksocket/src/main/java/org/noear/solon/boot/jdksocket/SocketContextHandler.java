package org.noear.solon.boot.jdksocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XMonitor;
import org.noear.solonclient.channel.SocketMessage;

public class SocketContextHandler {

    public void handle(SocketSession session, SocketMessage message) {
        if (message == null) {
            return;
        }

        try {
            handleDo(session, message);
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            XMonitor.sendError(null, ex);
        }
    }

    private void handleDo(SocketSession session, SocketMessage message) {
        SocketContext context = new SocketContext(session, message);

        try {
            XApp.global().tryHandle(context);

            context.commit();
        } catch (Throwable ex) {
            XMonitor.sendError(context, ex);
        }
    }
}
