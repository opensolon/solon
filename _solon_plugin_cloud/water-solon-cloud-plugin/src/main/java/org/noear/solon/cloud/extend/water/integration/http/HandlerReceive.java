package org.noear.solon.cloud.extend.water.integration.http;

import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.solon.cloud.extend.water.service.CloudEventServiceWaterImp;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.core.handle.MethodType;
import org.noear.water.WaterClient;
import org.noear.water.dso.MessageHandler;
import org.noear.water.model.MessageM;

/**
 * 消息接收处理（用签名的形式实现安全）//高频
 *
 * @author noear
 * @since 1.2
 */
public class HandlerReceive implements Handler, MessageHandler {
    CloudEventServiceWaterImp eventService;

    public HandlerReceive(CloudEventServiceWaterImp eventService) {
        this.eventService = eventService;
    }

    @Override
    public void handle(Context ctx) throws Throwable {
        try {
            if (MethodType.HEAD.name.equals(ctx.method())) {
                ctx.output("HEAD-OK");
                return;
            }

            String rst = WaterClient.Message.receiveMessage(ctx::param, eventService.getSeal(), this);
            ctx.output(rst);
        } catch (Throwable e) {
            e = Utils.throwableUnwrap(e);
            EventBus.push(e);
            ctx.output(e);
        }
    }

    @Override
    public boolean handle(MessageM msg) throws Throwable {
        Event event = null;
        if (msg.topic.contains(WaterProps.GROUP_TOPIC_SPLIT_MART)) {
            String[] groupAndTopic = msg.topic.split(WaterProps.GROUP_TOPIC_SPLIT_MART);
            event = new Event(groupAndTopic[1], msg.message);
            event.group(groupAndTopic[0]);
        } else {
            event = new Event(msg.topic, msg.message);
        }

        event.key(msg.key);
        event.tags(msg.tags);
        event.times(msg.times);

        return eventService.onReceive(msg.topic, event); // 可以不吃异常
    }
}