package org.noear.solon.extend.socketd;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;

/**
 * SocketD 上下文处理者
 *
 * @author noear
 * @since 1.0
 * */
class SocketContextHandler {

    public void handle(Session session, Message message, boolean messageIsString) {
        if (message == null) {
            return;
        }

        try {
            SocketContext ctx = new SocketContext(session, message, messageIsString);

            Solon.global().tryHandle(ctx);

            //不管有没有成功，都返回
            ctx.commit();
        } catch (Throwable ex) {
            //context 初始化时，可能会出错
            //
            EventBus.push(ex);
        }
    }
}
