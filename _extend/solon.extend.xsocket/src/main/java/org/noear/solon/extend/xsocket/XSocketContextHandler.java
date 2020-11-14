package org.noear.solon.extend.xsocket;

import org.noear.solon.XApp;
import org.noear.solon.core.XEventBus;
import org.noear.solon.core.XMethod;
import org.noear.solon.core.XMessage;
import org.noear.solon.core.XSession;

/**
 * XSocket 上下文处理者
 *
 * @author noear
 * @since 1.0
 * */
class XSocketContextHandler {

    public void handle(XSession session, XMessage message, boolean messageIsString) {
        if (message == null) {
            return;
        }

        try {
            XSocketContext ctx = new XSocketContext(session, message, messageIsString);

            XApp.global().tryHandle(ctx);

            //不管有没有成功，都返回
            ctx.commit();
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            XEventBus.push(ex);
        }
    }
}
