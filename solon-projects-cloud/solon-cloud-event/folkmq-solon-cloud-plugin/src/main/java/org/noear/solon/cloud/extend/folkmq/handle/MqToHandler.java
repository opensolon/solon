package org.noear.solon.cloud.extend.folkmq.handle;

import org.noear.folkmq.client.MqAlarm;
import org.noear.folkmq.client.MqConsumeHandler;
import org.noear.folkmq.client.MqMessageReceived;
import org.noear.folkmq.client.MqMessageReceivedImpl;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 转到 Handler 接口协议的 Listener（服务端、客户端，都可用）
 *
 * @author noear
 * @since 2.0
 */
public class MqToHandler implements MqConsumeHandler {
    private static final Logger log = LoggerFactory.getLogger(MqToHandler.class);


    @Override
    public void consume(MqMessageReceived message) throws Exception {
        if (Utils.isEmpty(message.getTag())) {
            log.warn("This message is missing route, tid={}", message.getTid());
            return;
        }

        try {
            MqContext ctx = new MqContext((MqMessageReceivedImpl) message);

            Solon.app().tryHandle(ctx);

            if (ctx.getHandled() || ctx.status() != 404) {
                ctx.commit();
            } else {
                message.response(new MqAlarm("No event handler was found! like code=404"));
            }
        } catch (Throwable e) {
            //context 初始化时，可能会出错
            //
            log.warn(e.getMessage(), e);

            message.response(new MqAlarm("No event handler was found! like code=404"));
        }
    }
}
