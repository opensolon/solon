package org.noear.solonx.socket.api;

import org.noear.solon.XApp;
import org.noear.solon.core.XEventBus;
import org.noear.solon.core.XMethod;

public class XSocketHandler {
    XMethod method;

    public XSocketHandler(XMethod method) {
        this.method = method;
    }

    public void handle(XSession session, XSocketMessage message, boolean messageIsString) {
        if (message == null) {
            return;
        }

        try {
            XSocketContext context = new XSocketContext(session, message, messageIsString, method);

            XApp.global().tryHandle(context);

            context.commit();
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            XEventBus.push(ex);
        }
    }
}
