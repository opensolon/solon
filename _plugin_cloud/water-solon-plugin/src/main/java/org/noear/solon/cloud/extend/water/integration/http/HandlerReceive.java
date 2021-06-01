package org.noear.solon.cloud.extend.water.integration.http;

import org.noear.solon.Utils;
import org.noear.solon.cloud.extend.water.service.CloudEventServiceWaterImp;
import org.noear.solon.cloud.model.Event;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.logging.utils.TagsMDC;
import org.noear.water.WaterClient;
import org.noear.water.dso.MessageHandler;
import org.noear.water.model.MessageM;

/**
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
            String rst = WaterClient.Message.receiveMessage(ctx::param, eventService.getSeal(), this);
            ctx.output(rst);
        } catch (Throwable ex) {
            ex = Utils.throwableUnwrap(ex);
            EventBus.push(ex);
            ctx.output(ex);
        }
    }

    @Override
    public boolean handler(MessageM msg) throws Throwable {
        Event event = new Event(msg.topic, msg.message);
        event.key(msg.key);
        event.tags(msg.tags);
        event.times(msg.times);

        return eventService.onReceive(event);
    }
}