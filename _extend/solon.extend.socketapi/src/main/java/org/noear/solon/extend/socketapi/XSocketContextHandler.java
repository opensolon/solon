package org.noear.solon.extend.socketapi;

import org.noear.solon.XApp;
import org.noear.solon.core.XEventBus;
import org.noear.solon.core.XMethod;

/**
 * XSocket 上下文处理者
 *
 * @author noear
 * @since 1.0
 * */
public class XSocketContextHandler {
    XMethod method;

    public XSocketContextHandler(XMethod method) {
        this.method = method;
    }

    public void handle(XSession session, XMessage message, boolean messageIsString) {
        if (message == null) {
            return;
        }

        try {
            XSocketContext ctx = new XSocketContext(session, message, messageIsString, method);

            XApp.global().tryHandle(ctx);

            if (ctx.getHandled() && ctx.status() != 404) {
                ctx.commit();
            }
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            XEventBus.push(ex);
        }
    }
}
