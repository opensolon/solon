package org.noear.solon.extend.xsocket;

import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.MessageSession;

/**
 * XSocket 上下文处理者
 *
 * @author noear
 * @since 1.0
 * */
class XSocketContextHandler {

    public void handle(MessageSession session, Message message, boolean messageIsString) {
        if (message == null) {
            return;
        }

        try {
            XSocketContext ctx = new XSocketContext(session, message, messageIsString);

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
